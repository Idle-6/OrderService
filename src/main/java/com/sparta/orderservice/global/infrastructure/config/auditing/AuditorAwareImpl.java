package com.sparta.orderservice.global.infrastructure.config.auditing;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        String accessToken = "";
        JwtUtil jwtUtil = new JwtUtil();

        HttpServletRequest servletRequest = (
                (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
        ).getRequest();

        Cookie[] cookies = servletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(JwtUtil.AUTHORIZATION_HEADER)) {
                accessToken = cookie.getValue();
            }
        }

        String at = jwtUtil.substringToken(accessToken);
        Claims info = jwtUtil.getUserInfoFromToken(at);
        String username = info.getSubject();
        //return Optional.of(userRepository.findByUsername(username).userPk);
        return Optional.of(Long.parseLong(username));
    }
}
