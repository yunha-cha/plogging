package com.website.security.jwt;

import com.website.entity.User;
import com.website.repository.UserRepository;
import com.website.user.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    @Value("${custom.setting.social-login-redirect-uri}")
    private String redirectUri;
    public OAuth2SuccessHandler(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User  user = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        User entity = userRepository.findByOauthProviderAndOauthId(provider, user.getAttribute("sub")).orElseThrow();
        entity.setLastLoginTime(LocalDateTime.now());
        userRepository.save(entity);
        String token = jwtUtil.createJwt(
                entity.getUserCode(),
                entity.getUserId(),
                entity.getRole(),
                1000 * 60 * 60 * 10L
        );
        String redirectUri = UriComponentsBuilder.fromUriString(this.redirectUri).queryParam("token",token).build().toUriString();
        response.sendRedirect(redirectUri);
    }
}
