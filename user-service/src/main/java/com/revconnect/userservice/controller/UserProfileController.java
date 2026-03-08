package com.revconnect.userservice.controller;

import com.revconnect.userservice.dto.CreateUserProfileRequest;
import com.revconnect.userservice.dto.UpdatePrivacyRequest;
import com.revconnect.userservice.dto.UpdateUserProfileRequest;
import com.revconnect.userservice.dto.UserProfileResponse;
import com.revconnect.userservice.entity.UserProfile;
import com.revconnect.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    @GetMapping("/suggestions")
    public List<UserProfileResponse> getSuggestions(@RequestParam Long userId){
        return userProfileService.getSuggestedUsers(userId);
    }
    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(@RequestParam Long userId){
        return userProfileService.getUserProfile(userId);
    }
    @GetMapping("/username/{username}")
    public UserProfileResponse getByUsername(@PathVariable String username){
        return userProfileService.getUserByUsername(username);
    }
    @PostMapping
    public UserProfileResponse createUserProfile(@RequestBody CreateUserProfileRequest request) {
        System.out.println("Received userId: " + request.getUserId());
        System.out.println("Received username: " + request.getUsername());
        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setUsername(request.getUsername());

        return userProfileService.createUserProfile(profile);
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        return userProfileService.getUserProfile(userId);
    }

    @PutMapping("/{userId}")
    public UserProfileResponse updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {

        return userProfileService.updateUserProfile(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserProfile(@PathVariable Long userId) {
        userProfileService.deleteUserProfile(userId);
    }

    @GetMapping("/search")
    public List<UserProfileResponse> searchUsers(@RequestParam String keyword) {
        return userProfileService.searchUsers(keyword);
    }

    @PutMapping("/{userId}/privacy")
    public void updatePrivacy(
            @PathVariable Long userId,
            @RequestBody UpdatePrivacyRequest request) {

        userProfileService.updatePrivacy(userId, request);
    }
}