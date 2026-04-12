package com.foodorder.dto;

import java.math.BigDecimal;

public record FoodResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        boolean available,
        Integer stockQuantity,
        Long categoryId,
        String categoryName
) {
}
