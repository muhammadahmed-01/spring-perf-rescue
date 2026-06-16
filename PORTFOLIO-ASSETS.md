# Portfolio Assets Pack

Upload these when pitching on Upwork, pinning on GitHub, or posting on LinkedIn. Every image maps to a measured result you can defend in a client call.

---

## Upwork Portfolio Title

**Spring Boot API Rescue: 111 Hidden Queries to 1 | k6 p95 1,605 ms to 696 ms | Production-Proven at Careem**

Alternative (shorter):

**Stop ORM Latency Bleed: 111 Queries to 1, k6 p95 Cut 2.3x (Spring Boot + PostgreSQL)**

---

## Upwork Portfolio Description

Your users feel every slow API response. Behind the scenes, Hibernate is often firing dozens or hundreds of hidden SELECTs on a single list endpoint, and nobody catches it until traffic spikes or the connection pool saturates.

This portfolio case study documents a real failure class and the fix, with numbers you can verify in Docker: k6 load tests show **p95 latency dropping from 1,605 ms to 696 ms** (100 VUs, 100k requests), SQL round trips falling from **111 to 1 per request**, and EXPLAIN ANALYZE showing exactly why. It mirrors the same audit process used on a production ORM path at Careem, where **p99 went from ~8 seconds to under 1 second** and **1,286 queries dropped to 2 batch calls**. Included: audit PDF template, Phase 1 SOW, and before/after screenshots ready for client delivery.

---

## Images to Upload (Order)

Lead with proof. Recruiters and clients decide in the first two images.

| Order | File | Caption for Upwork |
|-------|------|-------------------|
| 1 | `docs/images/query-count-comparison.png` | Root cause proof: 111 SQL round trips per request (N+1) reduced to 1 (JOIN FETCH) |
| 2 | `docs/images/k6-fixed-results.png` | After fix: k6 p95 696 ms, 100 VUs, 100k requests |
| 3 | `docs/images/k6-buggy-results.png` | Before fix: k6 p95 1,605 ms, same load profile |
| 4 | `docs/images/metrics-comparison.png` | Measured before/after: queries, p95 latency, throughput on same hardware |
| 5 | `docs/images/explain-buggy.png` | EXPLAIN ANALYZE: repeated seq scan on order_items per order (the hidden cost) |
| 6 | `docs/images/explain-fixed.png` | EXPLAIN ANALYZE: single hash join across orders, users, items |
| 7 (optional) | `docs/images/architecture-diagram.png` | Request flow: lazy loop vs JOIN FETCH fix path |

---

## GitHub README Pin Suggestion

**Repository:** https://github.com/muhammadahmed-01/spring-perf-rescue-lab

Pin the repo and lead the README with the metrics table and `docs/images/query-count-comparison.png`. Link to:

- `docs/PHASE-1-AUDIT-SOW.md` (proposal scope)
- `docs/audit-report-template.md` (sample deliverable)
- `PORTFOLIO-ASSETS.md` (this file)

**Pin description (short):** Production-proven Spring Boot ORM audit. 111 queries to 1, k6 p95 1,605 ms to 696 ms. Careem: p99 8s to under 1s.

---

## For Complex Projects (Positioning)

Real client systems combine ORM issues with caching, connection pools, external APIs, and infra limits. This case study does not promise a single JOIN FETCH fixes every production incident. It proves a **repeatable audit process**: capture baseline query count, run one EXPLAIN, prioritize P0/P1/P2, ship a minimal fix, and re-measure on the same hardware. Phase 1 scope is defined in `docs/PHASE-1-AUDIT-SOW.md`. Careem-scale work used the same sequence at larger data and traffic; deeper distributed or infra problems are scoped as follow-on work.

**Honest framing for founders:** Phase 1 de-risks the hire. You get measured evidence on one endpoint before committing to a larger engagement.

---

## LinkedIn Post Snippet

Most slow Spring Boot APIs are not slow code. They are slow databases doing work nobody counted.

I documented a production failure class end to end: Hibernate N+1 on a hot GET endpoint, **111 SQL calls per request down to 1**, **k6 p95 1,605 ms to 696 ms** under load. Same audit flow I used at Careem (p99 ~8s to under 1s, 1,286 queries to 2 batch calls).

Reproducible in Docker. Audit PDF template included. Link in comments.

---

## Related Files

| File | Use |
|------|-----|
| `UPWORK-BLURB.md` | Proposal paste block |
| `docs/CAREEM-WAR-STORY.md` | Interview / proposal hooks |
| `docs/FIRST-2-HOURS-CHECKLIST.md` | Show process on discovery calls |
| `docs/images/` | Screenshot source files for Upwork uploads |
