# Careem N+1 War Story (Internal Reference)

**Purpose:** Proposal and interview talking points. Facts only from production work. Not a fake client case study.

---

## Verified Production Facts

| Metric | Before | After |
|--------|--------|-------|
| p99 latency | ~8 seconds | under 1 second |
| SQL round trips (hot read path) | 1,286 | 2 batch calls |
| S3-related load (same initiative) | baseline | 83% to 88% reduction |

**Pattern:** Hibernate lazy loading on a list endpoint. Service layer iterated records and touched lazy associations (same class of bug as N+1 SELECT).

**Fix approach:** Batch fetch / eager fetch plan on the hot read path (production equivalent of JOIN FETCH or `@EntityGraph`), not a full rewrite.

---

## Investigation Path (Production vs Lab)

| Stage | Careem (production) | Portfolio lab |
|-------|---------------------|---------------|
| Symptom | p99 spikes on orders/list API under traffic | k6 p95 134 ms on `/api/orders/buggy` |
| SQL count | 1,286 queries traced via Hibernate stats | 111 queries via `X-Query-Count` |
| Root cause | Lazy association accessed in loop | `mapOrderWithLazyLoads` |
| Plan review | EXPLAIN on repeated item/user fetches | `docs/explain-analyze.md` |
| Fix | Batch/eager fetch on repository | `findAllOrdersWithItemsAndUser` JOIN FETCH |
| Validation | Staging stats + load test before deploy | k6 p95 17 ms, 1 query |
| Side win | S3 call reduction from fewer round trips | Not modeled in lab (out of scope) |

The lab scales the **same investigation sequence** down to 100 orders so numbers are reproducible on any laptop with Docker.

---

## What the Lab Does NOT Prove

- Careem-scale traffic, sharding, or multi-region behavior
- Exact 1,286 query reproduction (dataset and endpoint differ)
- S3 or CDN optimization (mentioned only as related production outcome)
- Team process, code review, or deploy timeline at Careem

Use Careem for **credibility and pattern recognition**. Use the lab for **measurable, clonable evidence**.

---

## Proposal Line Templates (No Em Dashes)

**Short hook:**
"I fixed this exact N+1 class at Careem (p99 8s to under 1s, 1,286 queries to 2). My portfolio lab reproduces the investigation with EXPLAIN and k6 numbers you can run locally."

**Scope anchor:**
"Phase 1 audit covers one hot endpoint: query count, EXPLAIN, P0/P1/P2 report. Same process I used on production ORM paths at Careem, scoped to your staging environment."

**Evidence offer:**
"I can share a sample audit PDF and live demo repo showing 111 queries reduced to 1 on an orders list, with p95 dropping from 134 ms to 17 ms on the same hardware."

**Complexity honesty:**
"Real client systems add infra, caching, and distributed calls. The audit SOW defines what Phase 1 guarantees: one endpoint, measured baseline, and a prioritized fix list. Deeper work is scoped separately."

---

## Interview Sound Bite

"At Careem we had a list endpoint doing over a thousand SQL round trips per request. I traced it with Hibernate statistics, confirmed repeated SELECTs in EXPLAIN, shipped a fetch-plan fix, and p99 went from about eight seconds to under one. The portfolio lab is the same bug at demo scale so clients can verify the methodology."
