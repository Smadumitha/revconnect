package com.revconnect.authservice.security;

import com.revconnect.authservice.client.UserClient;
import com.revconnect.authservice.dto.CreateUserProfileRequest;
import com.revconnect.authservice.entity.User;
import com.revconnect.authservice.repository.RefreshTokenRepository;
import com.revconnect.authservice.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    // Change this if your Angular app runs on a different port
    private static final String FRONTEND_URL = "http://localhost:4200/auth/oauth-callback";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Auto-generate unique username from email prefix
            String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
            String username = baseUsername;
            while (userRepository.findByUsername(username).isPresent()) {
                username = baseUsername + UUID.randomUUID().toString().substring(0, 4);
            }
            user = User.builder()
                    .email(email)
                    .username(username)
                    .provider("GOOGLE")
                    .role("PERSONAL")
                    .failedAttempts(0)
                    .accountLocked(false)
                    .build();
            user = userRepository.save(user);
            // Create profile in user-service
            try {
                userClient.createUserProfile(
                        new CreateUserProfileRequest(user.getId(), user.getUsername())
                );
            } catch (Exception ignored) {}
        }
        String accessToken = jwtUtil.generateToken(user.getUsername());
        // Redirect to Angular with all necessary query params
        String redirectUrl = FRONTEND_URL
                + "?token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&userId=" + user.getId()
                + "&username=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&displayName=" + URLEncoder.encode(name != null ? name : user.getUsername(), StandardCharsets.UTF_8)
                + "&role=" + user.getRole()
                + (picture != null ? "&profilePicture=" + URLEncoder.encode(picture, StandardCharsets.UTF_8) : "");
        response.sendRedirect(redirectUrl);
    }
}