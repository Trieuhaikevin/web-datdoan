package com.foodorder.dto;

public record CreateOrderItemRequest(Long foodId, Integer quantity) {
}
