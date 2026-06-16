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

**Why this matters in proposals:** Clients and hiring teams do not need another engineer who "knows Hibernate." They need someone who has traced 1,286 queries to a loop, fixed it, and measured the result in production.

---

## Investigation Path (Production vs Case Study)

| Stage | Careem (production) | Portfolio case study |
|-------|---------------------|----------------------|
| Symptom | p99 spikes on orders/list API under traffic | k6 p95 134 ms on `/api/orders/buggy` |
| SQL count | 1,286 queries traced via Hibernate stats | 111 queries via `X-Query-Count` |
| Root cause | Lazy association accessed in loop | `mapOrderWithLazyLoads` |
| Plan review | EXPLAIN on repeated item/user fetches | `docs/explain-analyze.md` |
| Fix | Batch/eager fetch on repository | `findAllOrdersWithItemsAndUser` JOIN FETCH |
| Validation | Staging stats + load test before deploy | k6 p95 17 ms, 1 query |
| Side win | S3 call reduction from fewer round trips | Not modeled in case study (out of scope) |

This case study scales the **same investigation sequence** down to 100 orders so numbers are reproducible on any laptop with Docker.

---

## What the Case Study Does NOT Prove

- Careem-scale traffic, sharding, or multi-region behavior
- Exact 1,286 query reproduction (dataset and endpoint differ)
- S3 or CDN optimization (mentioned only as related production outcome)
- Team process, code review, or deploy timeline at Careem

Use Careem for **credibility and pattern recognition**. Use the case study for **measurable, clonable evidence**.

---

## Proposal Line Templates (No Em Dashes)

**Short hook (lead with pain):**
"Your list endpoint probably looks fine in Postman and breaks under traffic. I fixed this exact N+1 class at Careem (p99 8s to under 1s, 1,286 queries to 2). My portfolio case study reproduces the investigation with EXPLAIN and k6 numbers you can run locally."

**Urgency without hype:**
"Every extra SELECT per row is latency your users pay for and connection pool capacity you burn. Phase 1 audit: one hot endpoint, measured baseline, P0/P1/P2 report. Same process I used on production ORM paths at Careem."

**Scope anchor:**
"Phase 1 covers one hot endpoint: query count, EXPLAIN, prioritized findings report. Clear boundaries, audit-ready deliverable. Deeper work (additional endpoints, CI guards, infra) scoped separately after Phase 1."

**Evidence offer (de-risk the hire):**
"Before you commit, review my sample audit PDF and run the reference repo in Docker: 111 queries reduced to 1 on an orders list, p95 134 ms to 17 ms on the same hardware. Evidence, not promises."

**For founders / PMs:**
"Invisible DB round trips are a silent tax on every user session. Phase 1 tells you exactly what that tax costs on your hottest endpoint and what to fix first."

**Complexity honesty:**
"Real client systems add infra, caching, and distributed calls. Phase 1 guarantees measured evidence on one endpoint, not a magic fix for everything. That honesty saves both of us time."

**Recruiter / hiring manager scan line:**
"Spring Boot, PostgreSQL, Hibernate. Production-proven: Careem p99 8s to under 1s. Portfolio: 111 queries to 1, p95 134 ms to 17 ms, reproducible in Docker."

---

## Interview Sound Bite

"At Careem we had a list endpoint doing over a thousand SQL round trips per request. I traced it with Hibernate statistics, confirmed repeated SELECTs in EXPLAIN, shipped a fetch-plan fix, and p99 went from about eight seconds to under one. The portfolio case study is the same bug at reference scale so you can verify the methodology on your machine."

**Follow-up if asked 'how do you know it was N+1':**
"Hibernate statistics showed the query count scaling with row count. EXPLAIN confirmed repeated SELECT patterns on the child table. After JOIN FETCH, count dropped to one and p95 moved in the same deploy window. We re-measured on staging before production."
