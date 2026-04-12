package com.foodorder.dto;

import java.math.BigDecimal;

public record FoodRequest(
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        Boolean available,
        Integer stockQuantity,
        Long categoryId
) {
}
