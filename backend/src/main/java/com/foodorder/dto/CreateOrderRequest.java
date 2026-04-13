package com.foodorder.dto;

import java.util.List;

public record CreateOrderRequest(
        Long userId,
        String receiverName,
        String receiverPhone,
        String deliveryAddress,
        List<CreateOrderItemRequest> items
) {
}
