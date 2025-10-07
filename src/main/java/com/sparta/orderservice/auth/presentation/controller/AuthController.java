package com.sparta.orderservice.auth.presentation.controller;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final JwtUtil jwtUtil;

    @GetMapping("/create-jwt")
    public String createJwt(HttpServletResponse res) {
        String username = "testUser";
        String role = "ROLE_USER";

        // accessToken 생성
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username);

        jwtUtil.addAccessTokenToHeader(accessToken, res);
        jwtUtil.addRefreshTokenToCookie(refreshToken, res);

        return "accessToken : " + accessToken + "\nrefreshToken : " + refreshToken;
    }

    @GetMapping("/get-jwt")
    public String getJwt(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String accessToken,
                         @CookieValue(JwtUtil.REFRESH_COOKIE_NAME) String refreshToken) {
        // accessToken substring
        String at = jwtUtil.substringToken(accessToken);

        // 토큰 검증
        // AccessToken
        if(!jwtUtil.validateToken(at, true)){
            throw new IllegalArgumentException("AccessToken Error");
        } else if(!jwtUtil.validateToken(refreshToken, false)) {
            throw new IllegalArgumentException("RefreshToken Error");
        }

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(at);
        // 사용자 username
        String username = info.getSubject();
        System.out.println("username = " + username);
        // 사용자 권한
        String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
        System.out.println("authority = " + authority);

        return "getJwt : " + username + ", " + authority;
    }
}
