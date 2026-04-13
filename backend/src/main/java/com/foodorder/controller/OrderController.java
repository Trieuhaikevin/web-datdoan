package com.foodorder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.dto.CreateOrderRequest;
import com.foodorder.dto.OrderResponse;
import com.foodorder.model.Order;
import com.foodorder.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<OrderResponse> getLatestOrderByUser(@PathVariable Long userId) {
        return orderService.getLatestOrderByUser(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @RequestParam Order.Status status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}
