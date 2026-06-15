#!/usr/bin/env pwsh
# Run k6 load tests for buggy and fixed endpoints.
param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$env:Path = [System.Environment]::GetEnvironmentVariable('Path','Machine') + ';' + [System.Environment]::GetEnvironmentVariable('Path','User')

Write-Host "Waiting for $BaseUrl/actuator/health ..."
for ($i = 1; $i -le 60; $i++) {
    try {
        $h = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -TimeoutSec 3
        if ($h.status -eq 'UP') { break }
    } catch {}
    Start-Sleep -Seconds 2
}

New-Item -ItemType Directory -Force -Path load/results | Out-Null

Write-Host "`n=== Buggy endpoint ==="
k6 run -e BASE_URL=$BaseUrl -e ENDPOINT=/api/orders/buggy -e MODE=buggy -e VUS=10 -e DURATION=30s load/k6-load.js

Write-Host "`n=== Fixed endpoint ==="
k6 run -e BASE_URL=$BaseUrl -e ENDPOINT=/api/orders/fixed -e MODE=fixed -e VUS=10 -e DURATION=30s load/k6-load.js

Write-Host "`nQuery counts (single request):"
(Invoke-RestMethod -Uri "$BaseUrl/api/orders/stats/buggy") | ConvertTo-Json -Compress
(Invoke-RestMethod -Uri "$BaseUrl/api/orders/stats/fixed") | ConvertTo-Json -Compress
