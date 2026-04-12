package com.foodorder.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long foodId,
        String foodName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) {
}
