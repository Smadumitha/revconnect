package com.revconnect.userservice.controller;

import com.revconnect.userservice.dto.CreateUserProfileRequest;
import com.revconnect.userservice.dto.UpdatePrivacyRequest;
import com.revconnect.userservice.dto.UpdateUserProfileRequest;
import com.revconnect.userservice.dto.UserProfileResponse;
import com.revconnect.userservice.entity.UserProfile;
import com.revconnect.userservice.repository.UserProfileRepository;
import com.revconnect.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;
    public UserProfileController(UserProfileService userProfileService, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
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
        System.out.println("Received role: " + request.getRole());
        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setUsername(request.getUsername());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getRole() != null) profile.setRole(request.getRole().toUpperCase());

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
    @PostMapping("/{userId}/profile-picture")
    public Map<String, String> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        // Store file locally under /uploads or use S3
        String filename = "user_" + userId + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String url = "/uploads/" + filename; // or your S3 URL
        profile.setProfilePictureUrl(url);
        userProfileRepository.save(profile);
        return Map.of("url", url);
    }
}