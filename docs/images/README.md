# Portfolio Images

Measured benchmark and EXPLAIN artifacts for Upwork, GitHub README, and LinkedIn. Every caption is defensible in a client call.

**Regenerate:** `docker compose up -d` then `pwsh scripts/capture-portfolio-assets.ps1`

---

| File | Caption | Best use |
|------|---------|----------|
| `query-count-comparison.png` | Root cause proof: 111 SQL round trips per request (N+1) vs 1 (JOIN FETCH) on 100 orders | Upwork image 1, README hero, proposal cover |
| `k6-fixed-results.png` | After fix: p95 17 ms under 10 concurrent users (k6, 30s) | Upwork image 2, "proof it works" slide |
| `k6-buggy-results.png` | Before fix: p95 134 ms, identical k6 load profile (10 VUs, 30s) | Upwork image 3, before/after contrast |
| `metrics-comparison.png` | Measured before/after bar chart: queries, p95 latency, throughput | LinkedIn, optional Upwork slot, audit PDF appendix |
| `explain-buggy.png` | EXPLAIN ANALYZE: repeated seq scan on `order_items` per order (hidden cost) | Upwork image 4, audit PDF appendix |
| `explain-fixed.png` | EXPLAIN ANALYZE: single hash join across orders, users, items | Upwork image 5, technical reviewer proof |
| `architecture-diagram.png` | Request flow: lazy association loop vs JOIN FETCH on hot read path | Upwork image 6, discovery call walkthrough |

**Upload tip:** Lead with `query-count-comparison.png`. It answers "what was wrong" in one glance. Follow with k6 after, then k6 before, for maximum before/after impact.

**Raw text captures** (for refresh): `raw/k6-buggy.txt`, `raw/k6-fixed.txt`, `raw/explain-*.txt`, `raw/query-stats-*.json`

**Measured date:** June 16, 2026 (re-run benchmarks to update)
