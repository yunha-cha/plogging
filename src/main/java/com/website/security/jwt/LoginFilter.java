package com.website.security.jwt;



import com.website.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


//1. 요청을 중간에서 가로채는 필터다.
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 1-1 가로채면 얘한테 들어온다.
     * UsernamePasswordAuthenticationToken 이라는 바구니에 담아서 AuthenticationManager 에게 던져준다.
     * @param request 요청에 body 부분을 받는 곳
     * @param response ??
     * @return ??
     * @throws AuthenticationException 이 메서드 안에서 에러나면 인증에러를 던짐
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userId = obtainUsername(request); //id를 빼옴
        String password = obtainPassword(request);   //pw를 빼옴

        //securiry에서 ID PW를 검증하기 위해선 token에 담아야함.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId,password,null);

        //token에 담은 검증을 위한 매니저로 전달 매니저 구현
        return authenticationManager.authenticate(authToken);
    }

    /**
     * 로그인에 성공하면 이 메서드가 실행된다.
     * @param request 요청
     * @param response 응답
     * @param chain .
     * @param authResult .
     * @throws IOException .
     * @throws ServletException .
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("로그인 성공");
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String accountId = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String accountRole = auth.getAuthority();
        Long accountCode = customUserDetails.getUserCode();
        String token = jwtUtil.createJwt(accountCode,accountId, accountRole,  1000 * 60 * 60 * 10L); //1000 = 1초 * 60 -> 1분 * 60 -> 1시간 * 10 -> 10시간
        String refreshToken = jwtUtil.createJwt(accountCode,accountId,accountRole,1000 * 60 * 60 * 24 * 7L);
        //여기에 User DB에 리프레시 토큰 추가하기
        //프론트에서는 401응답 (토큰 만료)를 받았을 때 /token/refresh 경로로 토큰 재발급 요청
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("Refresh-Token",refreshToken);
    }

    /**
     * 로그인에 실패하면 이 메서드가 실행된다.
     * @param request .
     * @param response .
     * @param failed .
     * @throws IOException .
     * @throws ServletException .
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("로그인 실패");
        response.setStatus(401);
    }
}
