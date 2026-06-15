package com.muhammadahmed.perf.web;

import com.muhammadahmed.perf.dto.OrderSummaryDto;
import com.muhammadahmed.perf.dto.QueryStatsResponse;
import com.muhammadahmed.perf.service.OrderQueryService;
import com.muhammadahmed.perf.support.QueryCountHolder;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final QueryCountHolder queryCountHolder;

    public OrderController(OrderQueryService orderQueryService, QueryCountHolder queryCountHolder) {
        this.orderQueryService = orderQueryService;
        this.queryCountHolder = queryCountHolder;
    }

    @GetMapping("/buggy")
    public ResponseEntity<List<OrderSummaryDto>> getOrdersBuggy() {
        List<OrderSummaryDto> orders = orderQueryService.listOrdersBuggy();
        return ResponseEntity.ok()
                .header("X-Query-Count", String.valueOf(queryCountHolder.getQueryCount()))
                .header("X-Perf-Mode", "buggy")
                .body(orders);
    }

    @GetMapping("/fixed")
    public ResponseEntity<List<OrderSummaryDto>> getOrdersFixed() {
        List<OrderSummaryDto> orders = orderQueryService.listOrdersFixed();
        return ResponseEntity.ok()
                .header("X-Query-Count", String.valueOf(queryCountHolder.getQueryCount()))
                .header("X-Perf-Mode", "fixed")
                .body(orders);
    }

    @GetMapping("/stats/buggy")
    public QueryStatsResponse statsBuggy() {
        return orderQueryService.fetchOrdersBuggy();
    }

    @GetMapping("/stats/fixed")
    public QueryStatsResponse statsFixed() {
        return orderQueryService.fetchOrdersFixed();
    }
}
