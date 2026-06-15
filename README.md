# Spring Boot + PostgreSQL Performance Rescue Lab

Reproducible demo of a Hibernate N+1 SELECT failure and JOIN FETCH fix, with measured query counts, k6 p95 latency, and EXPLAIN ANALYZE walkthrough.

**Proposal hook:** I fixed the same N+1 class at Careem (p99 8s to under 1s, 1,286 to 2 queries). Lab reproduces it with EXPLAIN and p95 numbers: `https://github.com/muhammadahmed-01/spring-perf-rescue-lab` 

---

## Problem

`GET /api/orders` loads 100 orders with 10 line items each. The buggy path uses `FetchType.LAZY` on `Order.items` and `Order.user`, then maps every order in a loop. Hibernate issues one SELECT for orders, then one SELECT per order for items, plus user lookups (111 SQL statements total on seeded data).

Under 10 concurrent users (k6), p95 latency hits **134 ms** with **62.2 req/s** throughput.

---

## Buggy code

Repository uses plain `findAll()` with no fetch plan:

```java
List<Order> orders = orderRepository.findAll();
return orders.stream().map(this::mapOrderWithLazyLoads).toList();
```

Service intentionally touches lazy associations per order:

```java
private OrderSummaryDto mapOrderWithLazyLoads(Order order) {
    String customerName = order.getUser().getName();      // N user SELECTs (L1-cached per user)
    List<OrderItemDto> items = order.getItems().stream()  // N item SELECTs
        .map(...)
        .toList();
    return toSummary(order, customerName, items);
}
```

**Endpoint:** `GET /api/orders/buggy`  
**Measured:** `X-Query-Count: 111` for 100 orders (see `/api/orders/stats/buggy`)

---

## Fix

`JOIN FETCH` on the hot read path loads orders, users, and items in a single round trip:

```java
@Query("""
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.user u
        JOIN FETCH o.items i
        ORDER BY o.id, i.id
        """)
List<Order> findAllOrdersWithItemsAndUser();
```

### Why JOIN FETCH (not @EntityGraph or batch size)

| Approach | Chosen? | Reason |
|----------|---------|--------|
| **JOIN FETCH** | Yes | One explicit query, easy to EXPLAIN, predictable for list endpoints |
| @EntityGraph | No | Same SQL, but less visible in code review; harder to show in audit |
| `default_batch_fetch_size` | No | Hides N+1 (drops to ~10 queries), bad for teaching the failure mode |
| DTO projection | Good at scale | Overkill for this lab; JOIN FETCH is the minimal fix |

**Endpoint:** `GET /api/orders/fixed`  
**Measured:** `X-Query-Count: 1` for 100 orders

---

## Metrics table (measured Jun 16, 2026, re-run via `scripts/capture-portfolio-assets.ps1`)

Load test: k6, 10 VUs, 30s, `http://localhost:8080`. Seed: 10 users x 10 orders x 10 items = 100 orders, 1,000 line items.

| Mode | SQL queries / request | k6 p95 (ms) | k6 avg (ms) | Throughput (req/s) |
|------|----------------------|-------------|-------------|-------------------|
| **Buggy** (`/api/orders/buggy`) | 111 | **134** | 60 | 62.2 |
| **Fixed** (`/api/orders/fixed`) | 1 | **17** | 11 | 89.3 |
| **Improvement** | 111x fewer queries | **7.9x faster p95** | 5.5x faster avg | 1.4x more throughput |

Query counts come from a Hibernate `StatementInspector` (response header `X-Query-Count` and `/api/orders/stats/*`).

---

## From lab to production

This repo is a **controlled demo**, not a copy of a client codebase. Production systems add connection pools, caches, read replicas, external APIs, and deploy history that can mask or multiply ORM issues.

**What the lab proves:** A repeatable audit sequence (baseline query count, one EXPLAIN, prioritized fix, re-measure on same hardware).

**What Phase 1 guarantees on real work:** One hot endpoint audited with measured evidence and a P0/P1/P2 report (`docs/PHASE-1-AUDIT-SOW.md`). Not a promise that every slow API is N+1.

**Careem context:** Same pattern class at production scale (p99 8s to under 1s, 1,286 to 2 queries). See `docs/CAREEM-WAR-STORY.md` for facts vs lab mapping.

**Day-one checklist:** `docs/FIRST-2-HOURS-CHECKLIST.md`

---

## Portfolio assets

Screenshots for Upwork, GitHub, and LinkedIn live in `docs/images/`:

| Image | Description |
|-------|-------------|
| [k6-buggy-results.png](docs/images/k6-buggy-results.png) | k6 output, buggy p95 134 ms |
| [k6-fixed-results.png](docs/images/k6-fixed-results.png) | k6 output, fixed p95 17 ms |
| [query-count-comparison.png](docs/images/query-count-comparison.png) | 111 vs 1 queries per request |
| [explain-buggy.png](docs/images/explain-buggy.png) | EXPLAIN ANALYZE N+1 item scan |
| [explain-fixed.png](docs/images/explain-fixed.png) | EXPLAIN ANALYZE JOIN path |
| [architecture-diagram.png](docs/images/architecture-diagram.png) | Buggy loop vs JOIN FETCH flow |
| [metrics-comparison.png](docs/images/metrics-comparison.png) | Before/after bar chart |

Full upload guide: [PORTFOLIO-ASSETS.md](PORTFOLIO-ASSETS.md). Visual one-pager: [docs/portfolio-preview.html](docs/portfolio-preview.html).

![Query count comparison](docs/images/query-count-comparison.png)

---

## How to run

### One command

```bash
cd spring-perf-rescue-lab
docker compose up --build
```

Wait for health, then:

```bash
curl http://localhost:8080/actuator/health
curl -s -D - http://localhost:8080/api/orders/buggy -o /dev/null | grep X-Query-Count
curl -s -D - http://localhost:8080/api/orders/fixed -o /dev/null | grep X-Query-Count
```

### Load test (k6)

```bash
k6 run -e BASE_URL=http://localhost:8080 -e ENDPOINT=/api/orders/buggy -e MODE=buggy load/k6-load.js
k6 run -e BASE_URL=http://localhost:8080 -e ENDPOINT=/api/orders/fixed -e MODE=fixed load/k6-load.js
```

Or run both:

```bash
bash scripts/run-benchmark.sh
```

### EXPLAIN ANALYZE

See [docs/explain-analyze.md](docs/explain-analyze.md) for SQL commands and sample output.

```bash
docker compose exec postgres psql -U perf -d perf_lab
```

---

## API endpoints

| Endpoint | Purpose |
|----------|---------|
| `GET /api/orders/buggy` | N+1 path, full JSON payload |
| `GET /api/orders/fixed` | JOIN FETCH path, full JSON payload |
| `GET /api/orders/stats/buggy` | Query count only (lightweight) |
| `GET /api/orders/stats/fixed` | Query count only (lightweight) |
| `GET /actuator/health` | Health check |

---

## Deliverables

| Artifact | Path |
|----------|------|
| Audit PDF template (sample filled) | [docs/audit-report-template.md](docs/audit-report-template.md) |
| Phase 1 audit SOW (proposals) | [docs/PHASE-1-AUDIT-SOW.md](docs/PHASE-1-AUDIT-SOW.md) |
| First 2 hours checklist | [docs/FIRST-2-HOURS-CHECKLIST.md](docs/FIRST-2-HOURS-CHECKLIST.md) |
| Careem war story (internal) | [docs/CAREEM-WAR-STORY.md](docs/CAREEM-WAR-STORY.md) |
| EXPLAIN ANALYZE guide | [docs/explain-analyze.md](docs/explain-analyze.md) |
| Portfolio pack | [PORTFOLIO-ASSETS.md](PORTFOLIO-ASSETS.md) |
| Upwork portfolio blurb | [UPWORK-BLURB.md](UPWORK-BLURB.md) |
| Portfolio images | [docs/images/](docs/images/) |
| k6 load script | [load/k6-load.js](load/k6-load.js) |

---

## Stack

- Java 17, Spring Boot 3.2, Spring Data JPA
- PostgreSQL 16
- Docker Compose (app + database)
- k6 for load testing

---

## Careem context

Production fix at Careem: p99 8s to under 1s, 1,286 SQL queries to 2 batch calls on a similar ORM read path. This lab scales the pattern down to portfolio size with reproducible numbers.
