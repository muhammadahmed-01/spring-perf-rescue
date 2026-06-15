#!/usr/bin/env pwsh
# Capture portfolio assets: k6 runs, EXPLAIN output, query counts, PNG images.
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ProjectRoot = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = "Stop"
$imagesDir = Join-Path $ProjectRoot "docs/images"
$rawDir = Join-Path $imagesDir "raw"
New-Item -ItemType Directory -Force -Path $imagesDir, $rawDir | Out-Null

Write-Host "Waiting for $BaseUrl/actuator/health ..."
for ($i = 1; $i -le 90; $i++) {
    try {
        $h = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -TimeoutSec 3
        if ($h.status -eq 'UP') { Write-Host "App is UP"; break }
    } catch {}
    Start-Sleep -Seconds 2
}

# Query counts
Write-Host "`n=== Query counts ==="
$buggyStats = Invoke-RestMethod -Uri "$BaseUrl/api/orders/stats/buggy"
$fixedStats = Invoke-RestMethod -Uri "$BaseUrl/api/orders/stats/fixed"
$buggyStats | ConvertTo-Json | Set-Content (Join-Path $rawDir "query-stats-buggy.json")
$fixedStats | ConvertTo-Json | Set-Content (Join-Path $rawDir "query-stats-fixed.json")
Write-Host "Buggy: $($buggyStats.queryCount) queries"
Write-Host "Fixed: $($fixedStats.queryCount) queries"

# k6 runs
$k6 = Get-Command k6 -ErrorAction SilentlyContinue
if ($k6) {
    Write-Host "`n=== k6 buggy ==="
    k6 run -e BASE_URL=$BaseUrl -e ENDPOINT=/api/orders/buggy -e MODE=buggy -e VUS=10 -e DURATION=30s (Join-Path $ProjectRoot "load/k6-load.js") 2>&1 | Tee-Object (Join-Path $rawDir "k6-buggy.txt")

    Write-Host "`n=== k6 fixed ==="
    k6 run -e BASE_URL=$BaseUrl -e ENDPOINT=/api/orders/fixed -e MODE=fixed -e VUS=10 -e DURATION=30s (Join-Path $ProjectRoot "load/k6-load.js") 2>&1 | Tee-Object (Join-Path $rawDir "k6-fixed.txt")
} else {
    Write-Host "k6 not found; skipping load tests. Install: choco install k6"
}

# EXPLAIN via docker postgres
Write-Host "`n=== EXPLAIN ANALYZE ==="
$explainBuggy = @"
EXPLAIN ANALYZE SELECT * FROM order_items WHERE order_id = 1;
"@
$explainFixed = @"
EXPLAIN ANALYZE
SELECT DISTINCT o.id, o.status, o.created_at, o.user_id,
       u.name, i.id, i.product_name, i.quantity, i.unit_price, i.order_id
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN order_items i ON i.order_id = o.id
ORDER BY o.id, i.id;
"@

try {
    $explainBuggyOut = docker compose exec -T postgres psql -U perf -d perf_lab -c $explainBuggy 2>&1
    $explainBuggyOut | Set-Content (Join-Path $rawDir "explain-buggy.txt")
    Write-Host $explainBuggyOut

    $explainFixedOut = docker compose exec -T postgres psql -U perf -d perf_lab -c $explainFixed 2>&1
    $explainFixedOut | Set-Content (Join-Path $rawDir "explain-fixed.txt")
    Write-Host $explainFixedOut
} catch {
    Write-Host "Docker EXPLAIN failed: $_"
}

# Generate PNGs from raw text
$pyScript = Join-Path $PSScriptRoot "generate-portfolio-images.py"
if (Test-Path $pyScript) {
    Write-Host "`n=== Generating PNG images ==="
    python $pyScript $ProjectRoot
} else {
    Write-Host "generate-portfolio-images.py not found"
}

Write-Host "`nDone. Images in $imagesDir"
