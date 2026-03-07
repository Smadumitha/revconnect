package com.revconnect.userservice.dto;

public class UserProfileResponse {
    private Long userId;
    private String username;
    private String bio;
    private String location;
    private String website;
    private String profilePictureUrl;
    private Boolean isPrivate;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long userId,
                               String username,
                               String bio,
                               String location,
                               String website,
                               String profilePictureUrl,
                               Boolean isPrivate) {
        this.userId = userId;
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.website = website;
        this.profilePictureUrl = profilePictureUrl;
        this.isPrivate = isPrivate;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}