package com.revconnect.authservice.service;

import com.revconnect.authservice.client.UserClient;
import com.revconnect.authservice.dto.*;
import com.revconnect.authservice.entity.RefreshToken;
import com.revconnect.authservice.entity.User;
import com.revconnect.authservice.repository.RefreshTokenRepository;
import com.revconnect.authservice.repository.UserRepository;
import com.revconnect.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    // ── REGISTER ─────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .privateAccount(request.isPrivateAccount())
                .securityQuestion(request.getSecurityQuestion())
                .securityAnswer(request.getSecurityAnswer())
                .provider("LOCAL")
                .failedAttempts(0)
                .accountLocked(false)
                .build();
        User savedUser = userRepository.save(user);
        // Create user profile in user-service
        CreateUserProfileRequest profileRequest =
                new CreateUserProfileRequest(savedUser.getId(), savedUser.getUsername());
        UserProfileResponse profile = userClient.createUserProfile(profileRequest);
        String accessToken = jwtUtil.generateToken(savedUser.getUsername());
        String refreshToken = createRefreshToken(savedUser);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .displayName(profile != null ? profile.getDisplayName() : savedUser.getUsername())
                .role(savedUser.getRole())
                .build();
    }
    // ── LOGIN ─────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        Optional<User> optionalUser =
                userRepository.findByEmailOrUsername(
                        request.getIdentifier(),
                        request.getIdentifier());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        if (user.isAccountLocked()) {
            throw new RuntimeException("Account locked due to failed attempts");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Invalid credentials");
        }
        user.setFailedAttempts(0);
        userRepository.save(user);
        // Fetch profile from user-service to get displayName
        UserProfileResponse profile = null;
        try {
            profile = userClient.getUserByUsername(user.getUsername());
        } catch (Exception ignored) {}
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = createRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(profile != null ? profile.getDisplayName() : user.getUsername())
                .profilePicture(profile != null ? profile.getProfilePicture() : null)
                .role(user.getRole())
                .build();
    }
    // ── REFRESH TOKEN ─────────────────────────────────────────
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken token =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        User user = token.getUser();
        String accessToken = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    // ── LOGOUT ────────────────────────────────────────────────
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
    // ── SECURITY QUESTION ─────────────────────────────────────
    public String getSecurityQuestion(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getSecurityQuestion();
    }
    // ── VALIDATE SECURITY ANSWER ──────────────────────────────
    public String validateSecurityAnswer(SecurityAnswerRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getSecurityAnswer().equalsIgnoreCase(request.getAnswer())) {
            throw new RuntimeException("Incorrect answer");
        }
        return "Answer verified";
    }
    // ── RESET PASSWORD ────────────────────────────────────────
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);
        return "Password reset successful";
    }
    // ── HELPER ────────────────────────────────────────────────
    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}