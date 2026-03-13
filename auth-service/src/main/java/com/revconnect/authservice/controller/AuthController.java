package com.revconnect.authservice.controller;

import com.revconnect.authservice.dto.*;
import com.revconnect.authservice.entity.RefreshToken;
import com.revconnect.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody String refreshToken) {

        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody String refreshToken) {

        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/security-question/{username}")
    public ResponseEntity<String> getSecurityQuestion(
            @PathVariable String username) {

        return ResponseEntity.ok(authService.getSecurityQuestion(username));
    }

    @PostMapping("/validate-answer")
    public ResponseEntity<String> validateAnswer(
            @RequestBody SecurityAnswerRequest request) {

        return ResponseEntity.ok(
                authService.validateSecurityAnswer(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.resetPassword(request));
    }
}