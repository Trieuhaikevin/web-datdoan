package com.foodorder.model;

import java.util.List;

public class CreateOrderRequestModel {
    private final Long userId;
    private final String receiverName;
    private final String receiverPhone;
    private final String deliveryAddress;
    private final List<CreateOrderItemRequestModel> items;

    public CreateOrderRequestModel(Long userId, String receiverName, String receiverPhone, String deliveryAddress, List<CreateOrderItemRequestModel> items) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<CreateOrderItemRequestModel> getItems() {
        return items;
    }
}
