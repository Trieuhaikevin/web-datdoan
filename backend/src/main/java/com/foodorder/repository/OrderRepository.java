package com.foodorder.repository;

import com.foodorder.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
