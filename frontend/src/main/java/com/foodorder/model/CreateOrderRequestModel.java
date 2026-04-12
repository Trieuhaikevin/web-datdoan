package com.foodorder.model;

import java.util.List;

public class CreateOrderRequestModel {
    private final Long userId;
    private final String deliveryAddress;
    private final List<CreateOrderItemRequestModel> items;

    public CreateOrderRequestModel(Long userId, String deliveryAddress, List<CreateOrderItemRequestModel> items) {
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<CreateOrderItemRequestModel> getItems() {
        return items;
    }
}
