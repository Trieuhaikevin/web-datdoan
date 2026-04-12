package com.foodorder.dto;

public record LoginResponse(
        Long userId,
        String fullName,
        String email,
        String role,
        String phone,
        String address,
        String message
) {
}
