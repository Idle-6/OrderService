package com.sparta.orderservice.global.infrastructure.security;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.infrastructure.util.TokenBlacklistMemoryStore;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistMemoryStore tokenBlacklistMemoryStore;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, TokenBlacklistMemoryStore tokenBlacklistMemoryStore) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistMemoryStore = tokenBlacklistMemoryStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 검증 및 인가 시작");

        String path = request.getRequestURI();

        // Access만 허용
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        if(StringUtils.hasText(accessToken)){
            if(!jwtUtil.validateToken(accessToken, true)){
                log.error("Token Error");
                return;
            }


            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            Long userId = info.get(JwtUtil.USER_ID, Long.class);
            if(userId != null && tokenBlacklistMemoryStore.isAccessTokenBlacklisted(userId)){
                log.error("deleted Token");
                throw new InsufficientAuthenticationException("삭제된 토큰입니다.");
            }

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        log.info("JWT 검증 및 인가 완료");
        filterChain.doFilter(request, response);
    }

    // 인증 처리
    private void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
