package com.revconnect.authservice.service;

import com.revconnect.authservice.client.UserClient;
import com.revconnect.authservice.dto.*;
import com.revconnect.authservice.entity.RefreshToken;
import com.revconnect.authservice.entity.User;
import com.revconnect.authservice.repository.RefreshTokenRepository;
import com.revconnect.authservice.repository.UserRepository;
import com.revconnect.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    /*
     REGISTER
     */

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

        // CALL USER SERVICE
        CreateUserProfileRequest profileRequest =
                new CreateUserProfileRequest(
                        savedUser.getId(),
                        savedUser.getUsername()
                );

//        userRepository.save(user);
//        userClient.createUserProfile(profileRequest);
        UserProfileResponse response = userClient.createUserProfile(profileRequest);

        System.out.println("User profile created: " + response.getUsername());
        System.out.println("Sending to user-service:");
        System.out.println(savedUser.getId());
        System.out.println(savedUser.getUsername());

        String accessToken = jwtUtil.generateToken(savedUser.getUsername());
        String refreshToken = createRefreshToken(savedUser);

        return new AuthResponse(accessToken, refreshToken);
    }

    /*
     LOGIN
     */

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

        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    /*
     CREATE REFRESH TOKEN
     */

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

    /*
     REFRESH TOKEN
     */

    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken token =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        String accessToken =
                jwtUtil.generateToken(token.getUser().getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    /*
     LOGOUT
     */

    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    /*
     GET SECURITY QUESTION
     */

    public String getSecurityQuestion(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSecurityQuestion();
    }

    /*
     VALIDATE SECURITY ANSWER
     */

    public String validateSecurityAnswer(SecurityAnswerRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getSecurityAnswer().equalsIgnoreCase(request.getAnswer())) {
            throw new RuntimeException("Incorrect answer");
        }

        return "Answer verified";
    }

    /*
     RESET PASSWORD
     */

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
}