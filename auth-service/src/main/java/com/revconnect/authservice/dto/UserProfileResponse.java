package com.revconnect.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String displayName;
    private String bio;
    private String location;
    private String website;
    private String profilePicture;
    private Boolean isPrivate;
}
