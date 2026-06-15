package com.muhammadahmed.perf.repository;

import com.muhammadahmed.perf.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN FETCH o.user u
            JOIN FETCH o.items i
            ORDER BY o.id, i.id
            """)
    List<Order> findAllOrdersWithItemsAndUser();
}
