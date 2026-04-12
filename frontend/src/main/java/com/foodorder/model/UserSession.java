package com.foodorder.model;

public class UserSession {
    private static final UserSession INSTANCE = new UserSession();

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String phone;
    private String address;

    private UserSession() {
    }

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public void setUser(Long userId, String fullName, String email, String role, String phone, String address) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    public void clear() {
        this.userId = null;
        this.fullName = null;
        this.email = null;
        this.role = null;
        this.phone = null;
        this.address = null;
    }

    public boolean isLoggedIn() {
        return userId != null;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
