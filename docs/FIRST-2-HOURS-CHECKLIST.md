# First 2 Hours Checklist: Real Client Performance Investigation

Use this on day one of a Spring Boot + PostgreSQL rescue. Each step maps to something demonstrated in `spring-perf-rescue-lab`.

---

## Hour 1: Symptom Capture and Baseline

| Step | Action | Lab equivalent |
|------|--------|----------------|
| 1 | Record symptom: which endpoint, p95/p99, when it started, traffic level | README problem section: `GET /api/orders`, p95 134 ms |
| 2 | Check recent deploys, config changes, migration scripts | Lab seeds fixed data; production needs deploy log |
| 3 | Hit endpoint once with curl; note response time and payload size | `curl http://localhost:8080/api/orders/buggy` |
| 4 | Capture response headers if app exposes query metrics | `X-Query-Count: 111` on buggy path |
| 5 | Enable or request slow query log threshold (e.g. 100 ms) | `docs/explain-analyze.md` SQL section |
| 6 | Turn on Hibernate statistics or JDBC proxy in staging | `StatementInspector` in lab (`SqlStatementCounter`) |

**Output by end of hour 1:** One written baseline: endpoint URL, single-request latency, query count if available, screenshot or log snippet.

---

## Hour 2: One EXPLAIN and One Trace

| Step | Action | Lab equivalent |
|------|--------|----------------|
| 7 | Identify the dominant SQL pattern (list + N lazy loads vs single JOIN) | `OrderQueryService.mapOrderWithLazyLoads` |
| 8 | Run **one** `EXPLAIN (ANALYZE, BUFFERS)` on the worst repeating query | `docs/explain-analyze.md` Step 2: `order_items WHERE order_id = 1` |
| 9 | Run **one** `EXPLAIN ANALYZE` on the ideal single-query shape | JOIN FETCH equivalent in `explain-analyze.md` |
| 10 | If APM available (Datadog, New Relic, Grafana): open one slow trace | Lab uses k6 + `X-Query-Count` as lightweight APM |
| 11 | Count SQL statements per request (stats endpoint, p6spy, or log grep) | `/api/orders/stats/buggy` returns `queryCount: 111` |
| 12 | Draft P0 hypothesis: N+1, missing index, pool exhaustion, or external call | Lab P0: N+1 on lazy `items` and `user` |

**Output by end of hour 2:** Findings draft with one EXPLAIN plan pasted, query count before any fix, proposed P0 item.

---

## Quick Commands (Copy for Client Staging)

```bash
# Health
curl -s http://STAGING_HOST/actuator/health

# Single request with timing
curl -s -w "\nHTTP %{http_code} in %{time_total}s\n" -D - http://STAGING_HOST/api/orders/buggy -o /dev/null

# Query stats if exposed
curl -s http://STAGING_HOST/api/orders/stats/buggy | jq .

# PostgreSQL EXPLAIN (replace connection)
psql -U app -d app_db -c "EXPLAIN ANALYZE SELECT * FROM order_items WHERE order_id = 1;"
```

---

## When to Escalate (Not ORM / Single Query)

Stop the ORM-only path and involve infra or platform when:

| Signal | Likely cause | Escalation |
|--------|--------------|------------|
| Query count is low (1 to 5) but p99 still high | External API, message queue, lock contention | Distributed trace, thread dump |
| CPU pegged, low DB wait | Application logic, serialization, GC | Profiler (async-profiler, JFR) |
| DB connections maxed, pool timeouts | Pool size, connection leaks, long transactions | Hikari metrics, `pg_stat_activity` |
| Slow on all endpoints after deploy | Config regression, JVM flags, bad dependency | Rollback test, diff deploy |
| Geo latency, multi-region | Network, read replica lag | Infra team, CDN, routing |
| Problem only under peak traffic | Autoscaling, rate limits, cache stampede | Load test + infra review |

The lab intentionally isolates **ORM N+1** so you can recognize it fast. Production often mixes issues; this checklist gets you evidence before proposing a fix scope.

---

## Lab Mapping Summary

| Real client step | Lab file / endpoint |
|------------------|---------------------|
| Buggy hot path | `GET /api/orders/buggy` |
| Fixed hot path | `GET /api/orders/fixed` |
| Query count proof | `GET /api/orders/stats/*`, header `X-Query-Count` |
| Load test baseline | `load/k6-load.js`, `scripts/run-benchmark.ps1` |
| EXPLAIN walkthrough | `docs/explain-analyze.md` |
| Client deliverable | `docs/audit-report-template.md` |
| Proposal scope | `docs/PHASE-1-AUDIT-SOW.md` |
