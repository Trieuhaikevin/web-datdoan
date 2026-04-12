package com.foodorder.dto;

import java.util.List;

public record CreateOrderRequest(
        Long userId,
        String deliveryAddress,
        List<CreateOrderItemRequest> items
) {
}
