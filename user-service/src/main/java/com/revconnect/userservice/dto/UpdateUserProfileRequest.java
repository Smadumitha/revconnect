package com.revconnect.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {
    private String displayName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Size(max = 200, message = "Website URL cannot exceed 200 characters")
    private String website;

    private Boolean isPrivate;

    // Extended profile fields
    private String category;
    private String industry;
    private String businessAddress;
    private String businessHours;
    private String contactEmail;
}