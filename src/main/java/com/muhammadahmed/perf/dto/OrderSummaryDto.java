package com.muhammadahmed.perf.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderSummaryDto(
        Long orderId,
        String customerName,
        String status,
        Instant createdAt,
        int itemCount,
        BigDecimal totalAmount,
        List<OrderItemDto> items
) {
    public record OrderItemDto(String productName, int quantity, BigDecimal unitPrice) {
    }
}
