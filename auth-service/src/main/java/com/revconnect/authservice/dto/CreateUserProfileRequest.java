package com.revconnect.authservice.dto;

public class CreateUserProfileRequest {

    private Long userId;
    private String username;

    public CreateUserProfileRequest() {}

    public CreateUserProfileRequest(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}