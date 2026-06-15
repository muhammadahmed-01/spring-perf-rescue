Muhammad Ahmed fixed a production N+1 query pattern at Careem that cut p99 latency from 8 seconds to under 1 second and reduced SQL round trips from 1,286 to 2 batch calls. This lab reproduces the same failure mode on a Spring Boot + PostgreSQL orders API with intentional lazy-loading on a hot GET endpoint. You get EXPLAIN ANALYZE walkthrough, k6 before/after p95 numbers, portfolio screenshots (`docs/images/`), and a one-page audit PDF template ready for client delivery.

**Portfolio visuals:** See `PORTFOLIO-ASSETS.md` for image upload order and captions. Key shots: k6 p95 134 ms vs 17 ms, query count 111 vs 1, EXPLAIN before/after.

**GitHub:** `https://github.com/muhammadahmed-01/spring-perf-rescue-lab`

**Proposal hook:** I fixed the same N+1 class at Careem (p99 8s to under 1s, 1,286 to 2 queries). Lab reproduces it with EXPLAIN + p95 numbers and portfolio images: https://github.com/muhammadahmed-01/spring-perf-rescue-lab
