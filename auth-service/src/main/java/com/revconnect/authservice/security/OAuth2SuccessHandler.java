package com.revconnect.authservice.security;

import com.revconnect.authservice.entity.User;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;

        if (userOptional.isPresent()) {

            user = userOptional.get();

        } else {

            user = User.builder()
                    .email(email)
                    .username(email)
                    .provider("GOOGLE")
                    .role("PERSONAL")
                    .build();

            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getUsername());

        // Return token directly in browser
        response.setContentType("text/plain");
        response.getWriter().write(
                "Google Login Successful\n\nJWT Token:\n" + token
        );
    }
}