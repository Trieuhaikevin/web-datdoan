package com.foodorder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodorder.model.Order;
import com.foodorder.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        return orderRepository.save(order);
    }

    public Order updateStatus(Long id, Order.Status status) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        orderRepository.deleteById(id);
    }
}