package com.hivecare.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.hivecare.model.User;
import com.hivecare.repository.UserRepository;
import com.hivecare.security.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect("http://localhost:3000/login?error=email_missing");
            return;
        }
        email = email.trim().toLowerCase();

        User user = userRepository.findByEmail(email);

        // OAuthUserService normally creates/links this account first.
        // This fallback makes the success handler safe even if that service did not.
        if (user == null) {
            user = new User();
            String name = oauthUser.getAttribute("name");
            if (name == null || name.trim().isEmpty()) name = "HiveCare User";
            user.setName(name.trim());
            user.setEmail(email);
            user.setPassword(null);
            user.setRole("USER");
            user.setProvider("GOOGLE");
            String sub = oauthUser.getAttribute("sub");
            user.setProviderId(sub);
            user.setBlocked(false);
            user.setRejectedBookings(0);
            user.setAvailable(false);
            user.setAvailabilitySet(false);
            user.setLoginAttempts(0);
            user.setLoginLockedUntil(null);
            user = userRepository.save(user);
        }

        if (user.isBlocked()) {
            response.sendRedirect("http://localhost:3000/login?error=account_blocked");
            return;
        }

        // IMPORTANT: role comes from the current MySQL row.
        // The frontend receives only a signed JWT and must call /api/users/me.
        String token = jwtService.generateToken(user);
        String redirectUrl = "http://localhost:3000/oauth-success?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}
