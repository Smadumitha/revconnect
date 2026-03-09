package com.revconnect.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    // User info — frontend needs these to avoid a second roundtrip
    private Long userId;
    private String username;
    private String email;
    private String displayName;
    private String profilePicture;
    private String role;
}
