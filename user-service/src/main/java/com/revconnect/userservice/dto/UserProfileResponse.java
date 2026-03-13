package com.revconnect.userservice.dto;

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
    private String email;
    private String role;
    private String bio;
    private String location;
    private String website;
    private String profilePicture;      // renamed from profilePictureUrl
    private Boolean isPrivate;
    private int followersCount;
    private int followingCount;
    private String category;
    private String industry;
    private String businessAddress;
    private String businessHours;
    private String contactEmail;
    private String createdAt;
}