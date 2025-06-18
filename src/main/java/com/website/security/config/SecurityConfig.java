package com.website.security.config;

import com.website.security.jwt.JWTFilter;
import com.website.security.jwt.JWTUtil;
import com.website.security.jwt.LoginFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    @Value("${custom.setting.cors}")
    private String cors;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
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
                // 이 체인은 /api/** 에만 매칭
                .securityMatcher("/api/**","/login","/favicon.ico")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig()))
                // /api/** 에 대해서는 모두 허용
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterAt(new LoginFilter(authenticationManager(),jwtUtil), UsernamePasswordAuthenticationFilter.class);
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
