package com.sparta.orderservice.global.infrastructure.security;
// 인증 : 유저 확인 (회원가입 / 로그인)

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.orderservice.auth.infrastructure.util.JwtProperties;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.dto.ReqLoginDtoV1;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.infrastructure.UserThreadLocal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/v1/auth/login"); // 로그인 경로 변경
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ReqLoginDtoV1 requestDto = new ObjectMapper().readValue(request.getInputStream(), ReqLoginDtoV1.class);

            // UsernamePasswordAuthenticationToken 생성
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserId();
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        UserThreadLocal.setUserId(userId);

        String accessToken = jwtUtil.createAccessToken(email, userId, role);
        String refreshToken = jwtUtil.createRefreshToken(email, userId);

        jwtUtil.addRefreshTokenToCookie(response, refreshToken);
        jwtUtil.addAccessTokenToHeader(response, accessToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message;
        if(failed instanceof BadCredentialsException){
            message = "아이디 혹은 비밀번호가 일치하지 않습니다.";
        } else {
            message = "인증에 실패하였습니다.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("error", "unauthorized");
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
