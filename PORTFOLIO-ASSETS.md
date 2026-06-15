# Portfolio Assets Pack

Use this file when uploading to Upwork, pinning on GitHub, or drafting LinkedIn posts.

---

## Upwork Portfolio Title

**Spring Boot + PostgreSQL N+1 Rescue: 111 Queries to 1, p95 134 ms to 17 ms**

---

## Upwork Portfolio Description (3 sentences)

I built a reproducible performance lab that demonstrates a Hibernate N+1 failure on a Spring Boot orders API and the JOIN FETCH fix with measured before/after numbers. The demo runs in Docker: k6 load tests show p95 latency dropping from 134 ms to 17 ms while SQL round trips fall from 111 to 1 per request. It mirrors the same investigation process I used on a production ORM path at Careem (p99 8s to under 1s, 1,286 queries to 2 batch calls).

---

## Images to Upload (Order)

| Order | File | Caption for Upwork |
|-------|------|-------------------|
| 1 | `docs/images/k6-buggy-results.png` | k6 load test: buggy endpoint p95 134 ms, 10 VUs, 30s |
| 2 | `docs/images/k6-fixed-results.png` | k6 load test: fixed endpoint p95 17 ms, same load profile |
| 3 | `docs/images/query-count-comparison.png` | SQL count per request: 111 (N+1) vs 1 (JOIN FETCH) |
| 4 | `docs/images/explain-buggy.png` | EXPLAIN ANALYZE: repeated seq scan on order_items per order |
| 5 | `docs/images/explain-fixed.png` | EXPLAIN ANALYZE: single hash join across orders, users, items |
| 6 | `docs/images/architecture-diagram.png` | Request flow: buggy lazy loop vs fixed JOIN FETCH path |
| 7 (optional) | `docs/images/metrics-comparison.png` | Before/after bar chart: queries, p95, throughput |

---

## GitHub README Pin Suggestion

**Repository:** https://github.com/muhammadahmed-01/spring-perf-rescue-lab

Pin the repo and lead the README with the metrics table and the `docs/images/query-count-comparison.png` screenshot. Link to:

- `docs/PHASE-1-AUDIT-SOW.md` (proposal scope)
- `docs/audit-report-template.md` (sample deliverable)
- `PORTFOLIO-ASSETS.md` (this file)

**Pin description (short):** Reproducible N+1 demo with k6 + EXPLAIN. 111 queries to 1, p95 134 ms to 17 ms.

---

## For Complex Projects (Positioning)

Real client systems combine ORM issues with caching, connection pools, external APIs, and infra limits. This lab does not promise a single JOIN FETCH fixes every production incident. It proves a **repeatable audit process**: capture baseline query count, run one EXPLAIN, prioritize P0/P1/P2, ship a minimal fix, and re-measure on the same hardware. Phase 1 scope is defined in `docs/PHASE-1-AUDIT-SOW.md`. Careem-scale work used the same sequence at larger data and traffic; deeper distributed or infra problems are scoped as follow-on work.

---

## LinkedIn Post Snippet

Shipped a portfolio lab: Spring Boot + PostgreSQL N+1 from 111 SQL calls to 1, p95 134 ms to 17 ms. Docker + k6 + EXPLAIN, same audit flow I used in production. Repo link in comments.

---

## Related Files

| File | Use |
|------|-----|
| `UPWORK-BLURB.md` | Proposal paste block |
| `docs/CAREEM-WAR-STORY.md` | Interview / proposal hooks |
| `docs/FIRST-2-HOURS-CHECKLIST.md` | Show process on discovery calls |
| `docs/portfolio-preview.html` | Single-page visual for screenshots |
