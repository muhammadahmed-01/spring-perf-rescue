# Performance Audit Report (Sample)

**Client:** Demo E-commerce Orders API (portfolio lab)  
**Auditor:** Muhammad Ahmed  
**Date:** June 16, 2026  
**Scope:** GET `/api/orders` hot path, PostgreSQL query plan review

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Findings](#findings)
3. [Priority Matrix (P0 / P1 / P2)](#priority-matrix-p0--p1--p2)
4. [Recommendations](#recommendations)

---

## Executive Summary

The orders listing endpoint suffered from a Hibernate N+1 SELECT pattern: one query for orders, then one query per order for line items, plus repeated user lookups. Under load (10 concurrent VUs, 100 orders seeded), p95 latency was **134 ms** with **111 SQL statements** per request. After applying `JOIN FETCH` on the hot read path, p95 dropped to **17 ms** with **1 SQL statement** per request. Throughput improved from **62.2 req/s** to **89.3 req/s** on the same hardware. Risk to production: high read amplification, connection pool pressure, and unpredictable p99 under traffic spikes.

---

## Findings

| ID | Area | Observation | Evidence |
|----|------|-------------|----------|
| F-1 | ORM mapping | `Order.items` and `Order.user` are `FetchType.LAZY`; service iterates all orders and touches both associations | `OrderQueryService.mapOrderWithLazyLoads` |
| F-2 | Query count | Buggy path executes 111 SELECTs (1 + 100 + 10) | `GET /api/orders/stats/buggy`, `X-Query-Count: 111` |
| F-3 | Query plan | Buggy item fetch scans `order_items` with a filter per order | `docs/explain-analyze.md` |
| F-4 | Fixed path | Single DISTINCT query with JOIN FETCH loads orders, users, and items | `OrderRepository.findAllOrdersWithItemsAndUser` |
| F-5 | Latency | k6 p95 improved 134 ms to 17 ms (87% reduction) | `load/k6-load.js`, see README metrics table |

---

## Priority Matrix (P0 / P1 / P2)

| Priority | Item | Impact | Effort |
|----------|------|--------|--------|
| **P0** | Replace lazy iteration on orders list with JOIN FETCH or `@EntityGraph` | High: removes 110 extra round trips per request | Low (1 repository method) |
| **P1** | Add integration test asserting SQL count ≤ 2 on orders list | Medium: prevents regression | Low |
| **P1** | Enable slow-query log + Hibernate statistics in staging | Medium: catches next hot path | Low |
| **P2** | Add index on `order_items(order_id)` if seq scans appear at scale | Medium at higher row counts | Low |
| **P2** | Pagination on orders list (limit 50) | Medium at scale | Low |

---

## Recommendations

1. **Ship JOIN FETCH fix to production** for the orders list endpoint; validate with Hibernate statistics before/after deploy.
2. **Add a CI guard** that fails if orders list executes more than 2 queries (pattern from `/api/orders/stats/*`).
3. **Run EXPLAIN ANALYZE** on staging after deploy; attach plans to ticket (commands in `docs/explain-analyze.md`).
4. **Load test** with k6 at expected peak VUs; track p95/p99 and pool wait time.
5. **Document** read-path fetch strategy in team wiki so new endpoints do not reintroduce N+1.

---

---

## PDF Export

**Option A (Pandoc):**

```bash
pandoc docs/audit-report-template.md -o audit-report.pdf --metadata title="Performance Audit Report"
```

**Option B (GitHub):** Open the file on GitHub, print to PDF from browser (Ctrl+P, Save as PDF). Use "Background graphics" for table borders.

**Option C (VS Code):** Markdown PDF extension, export `docs/audit-report-template.md`.

Replace client name, dates, and endpoint URLs before sending. Attach k6 summary JSON and EXPLAIN screenshots from `docs/images/` as appendix pages.

---

*Sample filled with portfolio lab measurements (Jun 16, 2026 re-run): 111 queries, p95 134 ms buggy; 1 query, p95 17 ms fixed. Re-run `scripts/capture-portfolio-assets.ps1` to refresh numbers.*
