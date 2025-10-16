package com.sparta.orderservice.global.infrastructure.security;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j(topic = "로그아웃")
@Transactional
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtLogoutHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        if(jwtUtil.validateToken(accessToken, true)) {
            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            userRepository.updateTokenExpiredAtById(info.get(JwtUtil.USER_ID, Long.class), System.currentTimeMillis());
            jwtUtil.expireRefreshCookie(response); // 쿠키 만료
        }
    }
}
