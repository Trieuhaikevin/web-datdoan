package com.foodorder.model;

import java.math.BigDecimal;

public class CartItem {
    private final Long foodId;
    private final String foodName;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartItem(Long foodId, String foodName, BigDecimal unitPrice, int quantity) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Long getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
