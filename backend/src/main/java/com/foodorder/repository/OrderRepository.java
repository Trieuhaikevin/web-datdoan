package com.foodorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    java.util.Optional<Order> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
