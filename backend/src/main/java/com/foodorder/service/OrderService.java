package com.foodorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.foodorder.dto.CreateOrderItemRequest;
import com.foodorder.dto.CreateOrderRequest;
import com.foodorder.dto.OrderItemResponse;
import com.foodorder.dto.OrderResponse;
import com.foodorder.model.Food;
import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;
import com.foodorder.model.User;
import com.foodorder.repository.FoodRepository;
import com.foodorder.repository.OrderRepository;
import com.foodorder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return orderRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        validateCreateOrderRequest(request);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy người dùng"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.deliveryAddress().trim());
        order.setStatus(Order.Status.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            validateOrderItem(itemRequest);

            Food food = foodRepository.findById(itemRequest.foodId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy món ăn"));

            if (!food.isAvailable() || food.getStockQuantity() <= 0) {
                throw new ResponseStatusException(BAD_REQUEST, food.getName() + " hiện không còn bán");
            }
            if (food.getStockQuantity() < itemRequest.quantity()) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Tồn kho không đủ cho món " + food.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFood(food);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setPrice(food.getPrice());
            orderItems.add(orderItem);

            BigDecimal subtotal = food.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalPrice = totalPrice.add(subtotal);

            int remainingStock = food.getStockQuantity() - itemRequest.quantity();
            food.setStockQuantity(remainingStock);
            if (remainingStock == 0) {
                food.setAvailable(false);
            }
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        return toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, Order.Status status) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        orderRepository.deleteById(id);
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null || request.userId() == null || !StringUtils.hasText(request.deliveryAddress())) {
            throw new ResponseStatusException(BAD_REQUEST, "Thiếu thông tin đơn hàng");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Giỏ hàng đang trống");
        }
    }

    private void validateOrderItem(CreateOrderItemRequest itemRequest) {
        if (itemRequest == null || itemRequest.foodId() == null || itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Thông tin món ăn không hợp lệ");
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(this::toItemResponse).toList();

        User user = order.getUser();
        return new OrderResponse(
                order.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getFullName() : null,
                user != null ? user.getEmail() : null,
                order.getDeliveryAddress(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemResponse(
                item.getFood() != null ? item.getFood().getId() : null,
                item.getFood() != null ? item.getFood().getName() : null,
                item.getQuantity(),
                price,
                subtotal
        );
    }
}
