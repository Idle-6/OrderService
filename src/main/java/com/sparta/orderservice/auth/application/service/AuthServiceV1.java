package com.sparta.orderservice.auth.application.service;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.dto.ResReissueDtoV1;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "auth Service")
public class AuthServiceV1 {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // RT 남은 수명이 이 값보다 짧으면 회전
    @Value("${jwt.rt-rotate-time}")
    private long RT_ROTATE_TIME;

    public ResReissueDtoV1 reissue(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new BadCredentialsException("NO_REFRESH_TOKEN");

        try {
            jwtUtil.validateToken(refreshToken, false);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
        Date exp = info.getExpiration();
        long userId = info.get(JwtUtil.USER_ID, Long.class);
        String email = info.getSubject();

        // 사용자 권한 조회
        UserDetails ud = userDetailsService.loadUserByUsername(email);
        UserRoleEnum role = ud.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority)
                .map(UserRoleEnum::fromAuthority).orElse(UserRoleEnum.USER);

        // AT 발급
        String newAT = jwtUtil.createAccessToken(email, userId, role);
        jwtUtil.addAccessTokenToHeader(response, newAT);

        long now = System.currentTimeMillis();
        boolean rotateRT = (exp.getTime() - now) <= RT_ROTATE_TIME;
        if(rotateRT) {
            String newRT = jwtUtil.createRefreshToken(email, userId);
            jwtUtil.addRefreshTokenToCookie(response, newRT);
        }

        ResReissueDtoV1 dto = new ResReissueDtoV1();
        dto.setRefreshRotated(rotateRT);

        return dto;
    }
}
