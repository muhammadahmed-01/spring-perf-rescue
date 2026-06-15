# EXPLAIN ANALYZE: Before and After

Seed data: **100 orders** (10 users x 10 orders), **1,000 line items** (10 per order).

Connect to PostgreSQL:

```bash
docker compose exec postgres psql -U perf -d perf_lab
```

## Before fix (N+1 pattern)

### Step 1: Load all orders (1 query)

```sql
EXPLAIN ANALYZE SELECT * FROM orders ORDER BY id;
```

**Sample output (measured on seeded data):**

```
                                                 QUERY PLAN
-------------------------------------------------------------------------------------------------------------
 Sort  (cost=16.39..16.74 rows=140 width=540) (actual time=0.046..0.050 rows=100 loops=1)
   Sort Key: id
   Sort Method: quicksort  Memory: 30kB
   ->  Seq Scan on orders  (cost=0.00..11.40 rows=140 width=540) (actual time=0.008..0.012 rows=100 loops=1)
 Planning Time: 0.462 ms
 Execution Time: 0.081 ms
```

### Step 2: Load items per order (repeated 100 times in app)

```sql
EXPLAIN ANALYZE SELECT * FROM order_items WHERE order_id = 1;
```

**Sample output:**

```
                                               QUERY PLAN
---------------------------------------------------------------------------------------------------------
 Seq Scan on order_items  (cost=0.00..21.50 rows=10 width=37) (actual time=0.015..0.075 rows=10 loops=1)
   Filter: (order_id = 1)
   Rows Removed by Filter: 990
 Planning Time: 0.744 ms
 Execution Time: 0.152 ms
```

**Cost model:** 1 orders scan + 100 item scans + 10 user PK lookups (L1-cached after first access per user) = **111 SQL round trips** (matches `GET /api/orders/stats/buggy`).

## After fix (JOIN FETCH)

Single query equivalent to `OrderRepository.findAllOrdersWithItemsAndUser()`:

```sql
EXPLAIN ANALYZE
SELECT DISTINCT o.id, o.status, o.created_at, o.user_id,
       u.name, i.id, i.product_name, i.quantity, i.unit_price, i.order_id
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN order_items i ON i.order_id = o.id
ORDER BY o.id, i.id;
```

**Sample output (measured on seeded data):**

```
                                                              QUERY PLAN
---------------------------------------------------------------------------------------------------------------------------------------
 Unique  (cost=98.96..123.96 rows=1000 width=1093) (actual time=0.824..1.042 rows=1000 loops=1)
   ->  Sort  (cost=98.96..101.46 rows=1000 width=1093) (actual time=0.823..0.862 rows=1000 loops=1)
         Sort Key: o.id, i.id, o.status, o.created_at, o.user_id, u.name, i.product_name, i.quantity, i.unit_price
         Sort Method: quicksort  Memory: 134kB
         ->  Hash Join  (cost=24.73..49.13 rows=1000 width=1093) (actual time=0.082..0.408 rows=1000 loops=1)
               Hash Cond: (o.user_id = u.id)
               ->  Hash Join  (cost=13.15..34.85 rows=1000 width=577) (actual time=0.051..0.262 rows=1000 loops=1)
                     Hash Cond: (i.order_id = o.id)
                     ->  Seq Scan on order_items i  (cost=0.00..19.00 rows=1000 width=37) (actual time=0.008..0.071 rows=1000 loops=1)
                     ->  Hash  (cost=11.40..11.40 rows=140 width=540) (actual time=0.029..0.030 rows=100 loops=1)
                           ->  Seq Scan on orders o  (cost=0.00..11.40 rows=140 width=540) (actual time=0.004..0.010 rows=100 loops=1)
               ->  Hash  (cost=10.70..10.70 rows=70 width=524) (actual time=0.017..0.017 rows=10 loops=1)
                     ->  Seq Scan on users u  (cost=0.00..10.70 rows=70 width=524) (actual time=0.008..0.010 rows=10 loops=1)
 Planning Time: 1.523 ms
 Execution Time: 1.281 ms
```

**Cost model:** **1 SQL round trip** per request (matches `GET /api/orders/stats/fixed`).

## Verify live query counts

```bash
curl http://localhost:8080/api/orders/stats/buggy
# {"queryCount":111,"orderCount":100,"mode":"buggy"}

curl http://localhost:8080/api/orders/stats/fixed
# {"queryCount":1,"orderCount":100,"mode":"fixed"}
```
