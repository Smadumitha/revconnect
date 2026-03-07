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

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  PrivacySettingsRepository privacySettingsRepository) {
        this.userProfileRepository = userProfileRepository;
        this.privacySettingsRepository = privacySettingsRepository;
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        return mapToResponse(profile);
    }

    @Override
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserProfileRequest request) {

        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());

        if (request.getIsPrivate() != null) {
            profile.setIsPrivate(request.getIsPrivate());
        }

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    public List<UserProfileResponse> searchUsers(String keyword) {

        List<UserProfile> users = userProfileRepository
                .findByUsernameContainingIgnoreCase(keyword);

        return users.stream()
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

    private UserProfileResponse mapToResponse(UserProfile profile) {

        return new UserProfileResponse(
                profile.getUserId(),
                profile.getUsername(),
                profile.getBio(),
                profile.getLocation(),
                profile.getWebsite(),
                profile.getProfilePictureUrl(),
                profile.getIsPrivate()
        );
    }
}