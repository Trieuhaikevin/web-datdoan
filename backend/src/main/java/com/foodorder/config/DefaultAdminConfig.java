package com.foodorder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin.default")
public record DefaultAdminConfig(
        boolean enabled,
        String email,
        String password,
        String fullName,
        String phone,
        String address
) {
}
