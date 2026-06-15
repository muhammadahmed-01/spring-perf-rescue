package com.muhammadahmed.perf.service;

import com.muhammadahmed.perf.domain.Order;
import com.muhammadahmed.perf.domain.OrderItem;
import com.muhammadahmed.perf.dto.OrderSummaryDto;
import com.muhammadahmed.perf.dto.QueryStatsResponse;
import com.muhammadahmed.perf.repository.OrderRepository;
import com.muhammadahmed.perf.support.QueryCountHolder;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final QueryCountHolder queryCountHolder;

    public OrderQueryService(OrderRepository orderRepository, QueryCountHolder queryCountHolder) {
        this.orderRepository = orderRepository;
        this.queryCountHolder = queryCountHolder;
    }

    @Transactional(readOnly = true)
    public QueryStatsResponse fetchOrdersBuggy() {
        queryCountHolder.reset();
        List<Order> orders = orderRepository.findAll();
        List<OrderSummaryDto> mapped = orders.stream().map(this::mapOrderWithLazyLoads).toList();
        return new QueryStatsResponse(queryCountHolder.getQueryCount(), mapped.size(), "buggy");
    }

    @Transactional(readOnly = true)
    public QueryStatsResponse fetchOrdersFixed() {
        queryCountHolder.reset();
        List<Order> orders = orderRepository.findAllOrdersWithItemsAndUser();
        List<OrderSummaryDto> mapped = orders.stream().map(this::mapOrderEagerLoaded).toList();
        return new QueryStatsResponse(queryCountHolder.getQueryCount(), mapped.size(), "fixed");
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDto> listOrdersBuggy() {
        queryCountHolder.reset();
        List<OrderSummaryDto> orders = orderRepository.findAll().stream()
                .map(this::mapOrderWithLazyLoads)
                .toList();
        return orders;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDto> listOrdersFixed() {
        queryCountHolder.reset();
        List<OrderSummaryDto> orders = orderRepository.findAllOrdersWithItemsAndUser().stream()
                .map(this::mapOrderEagerLoaded)
                .toList();
        return orders;
    }

    private OrderSummaryDto mapOrderWithLazyLoads(Order order) {
        // Intentional N+1: each access to lazy user and items triggers separate SELECTs.
        String customerName = order.getUser().getName();
        List<OrderSummaryDto.OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderSummaryDto.OrderItemDto(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();
        return toSummary(order, customerName, items);
    }

    private OrderSummaryDto mapOrderEagerLoaded(Order order) {
        String customerName = order.getUser().getName();
        List<OrderSummaryDto.OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderSummaryDto.OrderItemDto(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();
        return toSummary(order, customerName, items);
    }

    private OrderSummaryDto toSummary(
            Order order,
            String customerName,
            List<OrderSummaryDto.OrderItemDto> items) {
        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderSummaryDto(
                order.getId(),
                customerName,
                order.getStatus(),
                order.getCreatedAt(),
                items.size(),
                total,
                items);
    }
}
