# Portfolio Images

Captured benchmark and EXPLAIN artifacts for Upwork, GitHub README, and LinkedIn.

**Regenerate:** `docker compose up -d` then `pwsh scripts/capture-portfolio-assets.ps1`

---

| File | Caption | Best use |
|------|---------|----------|
| `k6-buggy-results.png` | k6 load test on `/api/orders/buggy`: p95 134 ms, 10 VUs, 30s | Upwork image 1, GitHub README |
| `k6-fixed-results.png` | k6 load test on `/api/orders/fixed`: p95 17 ms, same profile | Upwork image 2 |
| `query-count-comparison.png` | SQL round trips: 111 (N+1) vs 1 (JOIN FETCH) | Upwork image 3, README hero |
| `explain-buggy.png` | EXPLAIN ANALYZE: repeated seq scan on `order_items` per order | Upwork image 4, audit PDF appendix |
| `explain-fixed.png` | EXPLAIN ANALYZE: single hash join across orders, users, items | Upwork image 5 |
| `architecture-diagram.png` | Client to Spring Boot to PostgreSQL: lazy loop vs JOIN FETCH | Upwork image 6, proposals |
| `metrics-comparison.png` | Bar chart: queries, p95, throughput before/after | LinkedIn, optional Upwork slot |

**Raw text captures** (for refresh): `raw/k6-buggy.txt`, `raw/k6-fixed.txt`, `raw/explain-*.txt`, `raw/query-stats-*.json`

**Measured date:** June 16, 2026 (re-run benchmarks to update)
