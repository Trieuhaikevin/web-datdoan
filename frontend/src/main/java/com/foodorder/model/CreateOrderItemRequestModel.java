package com.foodorder.model;

public class CreateOrderItemRequestModel {
    private final Long foodId;
    private final Integer quantity;

    public CreateOrderItemRequestModel(Long foodId, Integer quantity) {
        this.foodId = foodId;
        this.quantity = quantity;
    }

    public Long getFoodId() {
        return foodId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
