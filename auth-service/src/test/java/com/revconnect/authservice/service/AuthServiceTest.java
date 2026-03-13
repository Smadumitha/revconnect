package com.revconnect.authservice.service;

import com.revconnect.authservice.client.UserClient;
import com.revconnect.authservice.dto.*;
import com.revconnect.authservice.entity.RefreshToken;
import com.revconnect.authservice.entity.User;
import com.revconnect.authservice.repository.RefreshTokenRepository;
import com.revconnect.authservice.repository.UserRepository;
import com.revconnect.authservice.security.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RefreshToken mockRefreshToken;

    @Before
    public void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .role("PERSONAL")
                .securityQuestion("Pet?")
                .securityAnswer("Fluffy")
                .failedAttempts(0)
                .accountLocked(false)
                .build();

        mockRefreshToken = RefreshToken.builder()
                .token("valid-refresh-token")
                .user(mockUser)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    public void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("PERSONAL");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("mock-access-token");
        when(userClient.createUserProfile(any(CreateUserProfileRequest.class))).thenReturn(null);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    public void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("testuser");
        request.setPassword("password123");

        when(userRepository.findByEmailOrUsername("testuser", "testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn("mock-access-token");
        when(userClient.getUserByUsername("testuser")).thenReturn(null);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    public void testRefreshToken_Success() {
        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(mockRefreshToken));
        when(jwtUtil.generateToken("testuser")).thenReturn("new-access-token");

        AuthResponse response = authService.refreshToken("valid-refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("valid-refresh-token", response.getRefreshToken());
    }

    @Test
    public void testGetSecurityQuestion_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        String question = authService.getSecurityQuestion("testuser");

        assertEquals("Pet?", question);
    }

    @Test
    public void testValidateSecurityAnswer_Success() {
        SecurityAnswerRequest request = new SecurityAnswerRequest();
        request.setUsername("testuser");
        request.setAnswer("Fluffy");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        String result = authService.validateSecurityAnswer(request);

        assertEquals("Answer verified", result);
    }

    @Test
    public void testResetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setUsername("testuser");
        request.setNewPassword("newpass123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("new_encoded_password");

        String result = authService.resetPassword(request);

        assertEquals("Password reset successful", result);
        verify(userRepository).save(mockUser);
    }

    @Test
    public void testLogout_Success() {
        doNothing().when(refreshTokenRepository).deleteByToken("valid-refresh-token");

        authService.logout("valid-refresh-token");

        verify(refreshTokenRepository, times(1)).deleteByToken("valid-refresh-token");
    }
}
