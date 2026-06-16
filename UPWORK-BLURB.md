# Upwork Portfolio Blurb

## Short version (paste into proposal)

Your API feels slow under load, but nobody can show you exactly why. I specialize in finding and fixing hidden ORM latency in Spring Boot + PostgreSQL systems, with measured before/after proof, not guesswork.

At Careem I resolved the same N+1 failure class on a production hot path: **p99 dropped from ~8 seconds to under 1 second**, SQL round trips fell from **1,286 to 2 batch calls**. This portfolio case study reproduces that investigation at reference scale: **111 queries to 1**, **p95 134 ms to 17 ms**, with EXPLAIN ANALYZE, k6 load tests, and an audit-ready PDF template you can review before we start.

**GitHub (verify locally):** https://github.com/muhammadahmed-01/spring-perf-rescue-lab

---

## Medium version (portfolio description)

If your Spring Boot API slows down when traffic rises, the database is often doing far more work than your team realizes. Hibernate lazy loading on list endpoints is a common culprit: one innocent-looking loop can trigger hundreds of SELECTs per request, draining connection pools and spiking p99 latency while single-request tests look fine.

Muhammad Ahmed fixed this exact pattern at Careem on a production orders path (**p99 ~8s to under 1s**, **1,286 queries to 2 batch calls**). This reference case study documents the full investigation on a Spring Boot + PostgreSQL orders API: root cause in code, EXPLAIN ANALYZE proof, k6 before/after numbers (**p95 134 ms to 17 ms**, **111 queries to 1**), and portfolio screenshots ready for your review.

**What you get on a Phase 1 audit:** One hot endpoint, measured baseline, prioritized P0/P1/P2 findings report, and optional minimal fix PR. Scope and boundaries are defined upfront in the SOW.

**Portfolio visuals:** See `PORTFOLIO-ASSETS.md` for image upload order and captions. Key shots: k6 p95 134 ms vs 17 ms, query count 111 vs 1, EXPLAIN before/after.

**GitHub:** https://github.com/muhammadahmed-01/spring-perf-rescue-lab

---

## Proposal hooks (pick one)

**Pain + proof:**
"I fixed this exact N+1 class at Careem (p99 8s to under 1s, 1,286 queries to 2). My portfolio case study reproduces the investigation with EXPLAIN and k6 numbers you can run locally: https://github.com/muhammadahmed-01/spring-perf-rescue-lab"

**Risk reduction:**
"Before we touch production code, I deliver a measured baseline and P0/P1/P2 audit report on one hot endpoint. Same process I used at Careem, scoped to your staging environment. Sample deliverable and reproducible proof: https://github.com/muhammadahmed-01/spring-perf-rescue-lab"

**Evidence over promises:**
"I can share a sample audit PDF and a reference repo showing 111 queries reduced to 1 on an orders list, with p95 dropping from 134 ms to 17 ms on the same hardware. Run it in Docker and verify before hiring: https://github.com/muhammadahmed-01/spring-perf-rescue-lab"
