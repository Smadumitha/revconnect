package com.revconnect.userservice.service.impl;

import com.revconnect.userservice.dto.UpdatePrivacyRequest;
import com.revconnect.userservice.dto.UpdateUserProfileRequest;
import com.revconnect.userservice.dto.UserProfileResponse;
import com.revconnect.userservice.entity.PrivacySettings;
import com.revconnect.userservice.entity.UserProfile;
import com.revconnect.userservice.repository.PrivacySettingsRepository;
import com.revconnect.userservice.repository.UserProfileRepository;
import com.revconnect.userservice.service.UserProfileService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final PrivacySettingsRepository privacySettingsRepository;
    private final com.revconnect.userservice.client.ConnectionClient connectionClient;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  PrivacySettingsRepository privacySettingsRepository,
                                  com.revconnect.userservice.client.ConnectionClient connectionClient) {
        this.userProfileRepository = userProfileRepository;
        this.privacySettingsRepository = privacySettingsRepository;
        this.connectionClient = connectionClient;
    }
    @Override
    public UserProfileResponse getUserByUsername(String username) {
        UserProfile user = userProfileRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }
    @Override
    public List<UserProfileResponse> getSuggestedUsers(Long userId) {
        return userProfileRepository.findAll().stream()
                .filter(user -> !user.getUserId().equals(userId))
                .limit(10)
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    // Fallback for missing profile to avoid 500 error
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    newProfile.setUsername("User_" + userId);
                    newProfile.setDisplayName("User " + userId);
                    newProfile.setRole("PERSONAL");
                    return newProfile;
                });
        return mapToResponse(profile);
    }
    @Override
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getIsPrivate() != null) profile.setIsPrivate(request.getIsPrivate());
        if (request.getCategory() != null) profile.setCategory(request.getCategory());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getBusinessAddress() != null) profile.setBusinessAddress(request.getBusinessAddress());
        if (request.getBusinessHours() != null) profile.setBusinessHours(request.getBusinessHours());
        if (request.getContactEmail() != null) profile.setContactEmail(request.getContactEmail());
        return mapToResponse(userProfileRepository.save(profile));
    }
    @Override
    public List<UserProfileResponse> searchUsers(String keyword) {
        return userProfileRepository
                .findByUsernameContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void deleteUserProfile(Long userId) {
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        userProfileRepository.delete(profile);
    }
    @Override
    public void updatePrivacy(Long userId, UpdatePrivacyRequest request) {
        PrivacySettings privacySettings = privacySettingsRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Privacy settings not found"));
        privacySettings.setIsPrivateAccount(request.getIsPrivateAccount());
        privacySettingsRepository.save(privacySettings);
    }
    @Override
    public UserProfileResponse createUserProfile(UserProfile profile) {
        UserProfile savedProfile = userProfileRepository.save(profile);
        return mapToResponse(savedProfile);
    }
    // ── MAPPER ────────────────────────────────────────────────
    private UserProfileResponse mapToResponse(UserProfile profile) {
        int followersCount = 0;
        int followingCount = 0;
        
        try {
            followersCount = connectionClient.getFollowers(profile.getUserId()).size();
            followingCount = connectionClient.getFollowing(profile.getUserId()).size();
        } catch (Exception e) {
            // Log error or ignore - keep counts at 0
        }

        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .displayName(profile.getDisplayName() != null ? profile.getDisplayName() : profile.getUsername())
                .email(profile.getEmail())
                .role(profile.getRole())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .website(profile.getWebsite())
                .profilePicture(profile.getProfilePictureUrl())
                .isPrivate(profile.getIsPrivate())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .category(profile.getCategory())
                .industry(profile.getIndustry())
                .businessAddress(profile.getBusinessAddress())
                .businessHours(profile.getBusinessHours())
                .contactEmail(profile.getContactEmail())
                .createdAt(profile.getCreatedAt() != null ? profile.getCreatedAt().toString() : null)
                .build();
    }
}