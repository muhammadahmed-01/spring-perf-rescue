import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ENDPOINT = __ENV.ENDPOINT || '/api/orders/buggy';
const MODE = __ENV.MODE || 'buggy';

export const options = {
  scenarios: {
    load: {
      executor: 'constant-vus',
      vus: __ENV.VUS ? Number(__ENV.VUS) : 10,
      duration: __ENV.DURATION || '30s',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<5000'],
  },
};

export default function () {
  const response = http.get(`${BASE_URL}${ENDPOINT}`);
  check(response, {
    'status is 200': (r) => r.status === 200,
    'has query count header': (r) => r.headers['X-Query-Count'] !== undefined,
  });
  sleep(0.1);
}

export function handleSummary(data) {
  const p95 = data.metrics.http_req_duration.values['p(95)'];
  const p99 = data.metrics.http_req_duration.values['p(99)'];
  const avg = data.metrics.http_req_duration.values.avg;
  const rps = data.metrics.http_reqs.values.rate;

  const summary = {
    mode: MODE,
    endpoint: ENDPOINT,
    p95_ms: Math.round(p95),
    p99_ms: Math.round(p99),
    avg_ms: Math.round(avg),
    rps: Number(rps.toFixed(2)),
    timestamp: new Date().toISOString(),
  };

  return {
    stdout: JSON.stringify(summary, null, 2) + '\n',
    [`load/results/${MODE}-summary.json`]: JSON.stringify(summary, null, 2),
  };
}
