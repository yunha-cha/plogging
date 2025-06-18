package com.website.security.controller;


import com.website.security.dto.CustomUserDetails;
import com.website.security.jwt.JWTUtil;
import com.website.security.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public class RefreshTokenController {
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public RefreshTokenController(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> tokenRequest){
        String refreshToken = tokenRequest.get("refreshToken");

        if(!jwtUtil.isExpired(refreshToken)){
            //여기서 if 문으로 DB에 저장된 유저의 리프레시 토큰이 일치하는지 확인하기
            String accountId = jwtUtil.getUsername(refreshToken);
            CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserByUsername(accountId);

            String newAccessToken = jwtUtil.createJwt(user.getUserCode(), user.getUsername(), user.getAuthorities().iterator().next().getAuthority(),1000 * 60 * 60 *10L);
            return ResponseEntity.ok().header("Authorization","Bearer "+newAccessToken).body("새로운 토큰이 발급되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 만료되었습니다.");
        }
    }
}
