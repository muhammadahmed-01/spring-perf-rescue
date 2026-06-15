#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Waiting for ${BASE_URL}/actuator/health ..."
for i in $(seq 1 60); do
  if curl -sf "${BASE_URL}/actuator/health" >/dev/null; then
    break
  fi
  sleep 2
done

mkdir -p load/results

echo
echo "=== Buggy endpoint ==="
k6 run -e BASE_URL="${BASE_URL}" -e ENDPOINT=/api/orders/buggy -e MODE=buggy load/k6-load.js

echo
echo "=== Fixed endpoint ==="
k6 run -e BASE_URL="${BASE_URL}" -e ENDPOINT=/api/orders/fixed -e MODE=fixed load/k6-load.js

echo
echo "Query counts (single request):"
curl -s "${BASE_URL}/api/orders/stats/buggy"
echo
curl -s "${BASE_URL}/api/orders/stats/fixed"
echo
