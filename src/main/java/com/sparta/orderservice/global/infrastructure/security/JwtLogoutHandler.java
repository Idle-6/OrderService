package com.sparta.orderservice.global.infrastructure.security;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.infrastructure.util.TokenBlacklistMemoryStore;
import com.sparta.orderservice.user.infrastructure.UserThreadLocal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j(topic = "로그아웃")
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtUtil jwtUtil;
    private final TokenBlacklistMemoryStore tokenBlacklistMemoryStore;

    public JwtLogoutHandler(JwtUtil jwtUtil, TokenBlacklistMemoryStore tokenBlacklistMemoryStore) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistMemoryStore = tokenBlacklistMemoryStore;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        Claims info = jwtUtil.getUserInfoFromToken(accessToken);

        // 남은 만료 시간까지 거부
        tokenBlacklistMemoryStore.addBlacklist(info.get(JwtUtil.USER_ID, Long.class), info.getExpiration().getTime());
        jwtUtil.expireRefreshCookie(response); // 쿠키 만료
        UserThreadLocal.removeUserId();
    }
}
