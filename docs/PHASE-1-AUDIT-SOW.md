# Phase 1 Performance Audit: Statement of Work

**Service:** Spring Boot / Hibernate / PostgreSQL hot-path performance audit  
**Prepared by:** Muhammad Ahmed  
**Version:** Portfolio template (replace client name and dates per proposal)

---

## Overview

Your API is costing you users and infrastructure budget every time a hot endpoint fires too many database round trips. This is a fixed-scope audit of **one production hot endpoint** where latency or database load is suspected.

You receive a prioritized findings report with **measured evidence**, not generic advice. Baseline numbers, root cause in code, EXPLAIN proof, and a clear P0/P1/P2 action list. Same investigation sequence used on production ORM paths at Careem (p99 ~8s to under 1s, 1,286 queries to 2 batch calls).

**What Phase 1 de-risks:** You know exactly what is wrong, how bad it is, and what to fix first, before committing to a larger engagement.

---

## Scope (Included)

| Item | Detail |
|------|--------|
| **One hot endpoint** | Single read or write path (e.g. orders list, dashboard aggregate) |
| **Symptom capture** | Baseline latency, error rate, traffic pattern, recent deploys |
| **Query analysis** | Hibernate statistics or equivalent SQL count per request |
| **EXPLAIN ANALYZE** | At least one before-plan on the slow query pattern |
| **Findings report** | P0 / P1 / P2 priority matrix with evidence links |
| **Optional fix PR** | Minimal patch on the audited path (repository or fetch plan change) |
| **Handoff call** | 30-minute walkthrough of findings and next steps |

**Typical timeline:** 3 to 5 business days from access grant to final report delivery.

---

## Out of Scope

Clear boundaries protect both of us. Phase 1 is an audit, not a rewrite.

- Full application rewrite or architecture migration
- Kubernetes, Terraform, or cloud infra tuning
- Greenfield development or new feature work
- Mobile app performance (iOS / Android)
- SOC 2, HIPAA, or compliance audit work
- Multi-region or distributed tracing deep dives (escalation path available separately)
- Database vendor migration (e.g. PostgreSQL to Aurora)

Follow-on sprints cover additional endpoints, CI guards, and load test suites. Scoped separately after Phase 1 findings.

---

## Deliverables

1. **Baseline snapshot** (query count, p95 latency, sample EXPLAIN plan)
2. **Audit report** (executive summary, findings table, P0/P1/P2 matrix)
3. **Reproduction steps** (curl/k6 commands or staging URL for validation)
4. **Optional:** GitHub PR with JOIN FETCH, `@EntityGraph`, or projection fix on audited endpoint
5. **30-minute handoff** (screen share or recorded Loom)

Every deliverable is audit-ready: your team can verify numbers on staging before and after any fix.

---

## Pricing

| Package | Price | Notes |
|---------|-------|-------|
| **Intro audit (portfolio rate)** | $399 to $499 | One endpoint, report only |
| **Audit + fix PR** | $699 to $899 | Includes minimal code change and re-measurement |
| **Follow-on sprint** | Scoped separately | Additional endpoints, CI guards, load test suite |

*Final price confirmed in Upwork proposal after brief discovery call.*

---

## Acceptance Criteria

The audit is complete when you can answer three questions with evidence: how slow is it, why is it slow, and what do we fix first?

1. **Baseline is documented** with measured query count and latency on the hot endpoint (not estimates).
2. **Root cause is identified** with code reference and at least one EXPLAIN ANALYZE or SQL trace.
3. **Priority matrix is delivered** with at least one P0 item if a fixable ORM or query issue exists.
4. **Reproduction is verified** by client or auditor on staging (curl header, stats endpoint, or APM screenshot).
5. **Optional fix PR** (if included) shows before/after query count improvement on the same hardware.

---

## Client Responsibilities

- Read-only staging access or reproducible local environment (Docker Compose acceptable)
- One technical contact for context (symptoms, deploy history, peak traffic)
- Response within 24 hours on blocking access questions

---

## Sample Reference

This SOW maps to the portfolio case study ([spring-perf-rescue-lab](https://github.com/muhammadahmed-01/spring-perf-rescue-lab)): 111 queries to 1, p95 134 ms to 17 ms on an N+1 orders list. Production audits follow the same investigation path at larger scale.

**Portfolio reference:** [spring-perf-rescue-lab](https://github.com/muhammadahmed-01/spring-perf-rescue-lab)

**Sample audit report:** [docs/audit-report-template.md](audit-report-template.md)
