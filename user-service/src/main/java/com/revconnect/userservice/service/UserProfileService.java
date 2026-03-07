package com.revconnect.userservice.service;

import com.revconnect.userservice.dto.UpdatePrivacyRequest;
import com.revconnect.userservice.dto.UpdateUserProfileRequest;
import com.revconnect.userservice.dto.UserProfileResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse getUserProfile(Long userId);

    UserProfileResponse updateUserProfile(Long userId, UpdateUserProfileRequest request);

    List<UserProfileResponse> searchUsers(String keyword);

    void deleteUserProfile(Long userId);

    void updatePrivacy(Long userId, UpdatePrivacyRequest request);

}