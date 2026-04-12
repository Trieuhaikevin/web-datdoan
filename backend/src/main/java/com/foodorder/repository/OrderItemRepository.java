package com.foodorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodorder.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByFood_Id(Long foodId);

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
           "FROM OrderItem oi " +
           "WHERE oi.food.id = ?1 " +
           "AND oi.order.status IN ('PENDING', 'CONFIRMED', 'DELIVERING')")
    boolean existsByFood_IdAndOrderNotDelivered(Long foodId);
}