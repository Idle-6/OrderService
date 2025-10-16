package com.sparta.orderservice.global.infrastructure.security;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.advice.AuthErrorCode;
import com.sparta.orderservice.auth.presentation.advice.AuthException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
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

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 검증 및 인가 시작");

        String path = request.getRequestURI();
        
        // 인가 필요없는 요청들 건너뛰기
        if(path.equals("/") || path.startsWith("/v1/auth/") || path.startsWith("/v1/users/sign-up")){
            log.info("JWT 검증 및 인가 skip");
            filterChain.doFilter(request, response);
            return;
        }

        // Access만 허용
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        if(StringUtils.hasText(accessToken)){
            jwtUtil.validateToken(accessToken, true);

            Claims claims = jwtUtil.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(claims);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw e;
            }
        }

        log.info("JWT 검증 및 인가 완료");
        filterChain.doFilter(request, response);
    }

    // 인증 처리
    private void setAuthentication(Claims claims) {
        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 만료 시간 검증 (JWT vs DB)
        validateTokenExpiration((UserDetailsImpl) userDetails, claims);

        // 인증 객체 생성
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // token_expired_at 이전에 발급된 토큰 무효화
    private void validateTokenExpiration(UserDetailsImpl userDetails, Claims claims) {
        if (!(userDetails instanceof UserDetailsImpl)) {
            throw new AuthException(AuthErrorCode.AUTH_INVALID_USER_DETAILS);
        }

        Long dbExp = userDetails.getTokenExpiredAt();
        // 만료된 토큰 없음 -> 모든 토큰 허용
        if (dbExp == null) {
            return;
        }

        Long jwtIat = claims.getIssuedAt().getTime();
        if (jwtIat == null) {
            throw new AuthException(AuthErrorCode.AUTH_INVALID_CLAIMS);
        }

        if (jwtIat <= dbExp) {
            throw new AuthException(AuthErrorCode.AUTH_EXPIRED_TOKEN);
        }
    }
}
