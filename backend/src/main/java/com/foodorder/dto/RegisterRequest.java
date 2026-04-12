package com.foodorder.dto;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        String phone,
        String address
) {
}
