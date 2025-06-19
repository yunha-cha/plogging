package com.website.security.config;

import com.website.security.jwt.JWTFilter;
import com.website.security.jwt.JWTUtil;
import com.website.security.jwt.LoginFilter;
import com.website.security.jwt.OAuth2SuccessHandler;
import com.website.security.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    @Value("${custom.setting.cors}")
    private String cors;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, OAuth2SuccessHandler oAuth2SuccessHandler, CustomOidcUserService customOidcUserService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 1) /api/** 요청만 처리하는 체인: 완전 공용, JWTFilter 없음
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/**",
                        "/login/**",               // 기존 로그인
                        "/favicon.ico",
                        "/oauth2/**",              // OAuth2 인가요청, 콜백 모두 포함
                        "/login/oauth2/**"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig()))
                .authorizeHttpRequests(auth -> auth
                        // 퍼블릭으로 열어둘 URL들
                        .requestMatchers(
                                "/api/**",
                                "/login/**",
                                "/oauth2/authorization/google",        // 구글 인가 요청
                                "/login/oauth2/code/google",           // 구글 콜백
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 기존 로그인 필터
                .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 인가 코드 받으러 보낼 URI
                        .authorizationEndpoint(authz ->
                                authz.baseUri("/oauth2/authorization"))
                        // 구글이 콜백을 보낼 URI
                        .redirectionEndpoint(redir ->
                                redir.baseUri("/login/oauth2/code/*"))
                        // UserInfo 로부터 프로필 추출 → 매핑
                        .userInfoEndpoint(ui ->
                                ui.oidcUserService(customOidcUserService))
                        // 성공 시 JWT 발급 핸들러
                        .successHandler(oAuth2SuccessHandler)
                        // 실패 시 리다이렉트/로깅 필요하면 설정
                        .failureHandler((req, res, ex) -> {
                            res.sendRedirect("/login?error");
                        })
                );

        return http.build();
    }

    // 2) 그 외 모든 요청: JWTFilter 적용 + 인증 필요
    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception {
        http
                // /api/** 를 제외한 나머지 요청에만 매칭
                .securityMatcher("/auth/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfig()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 검증 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(new JWTFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    @Order(3)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // 모든 요청을 포함
                .authorizeHttpRequests(auth -> auth.anyRequest().denyAll());
        return http.build();
    }

    // CORS 공통 설정 메서드
    private CorsConfigurationSource corsConfig() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList(cors));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList("Authorization"));
            config.setMaxAge(3600L);
            return config;
        };
    }
}
