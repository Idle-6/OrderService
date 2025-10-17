package com.sparta.orderservice.auth.application.service;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.advice.AuthErrorCode;
import com.sparta.orderservice.auth.presentation.advice.AuthException;
import com.sparta.orderservice.auth.presentation.dto.ResReissueDtoV1;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceV1Test {

    @Mock
    JwtUtil jwtUtil;
    @Mock
    UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    AuthServiceV1 authServiceV1;

    String refreshToken = "refreshToken";
    String email = "user@test.com";
    long userId = 1L;

    void setup(Long time) {
        // validationToken 통과
        doNothing().when(jwtUtil).validateToken(refreshToken, false);

        Claims claims = mock(Claims.class);
        when(jwtUtil.getUserInfoFromToken(refreshToken)).thenReturn(claims);
        when(claims.getExpiration().getTime()).thenReturn(System.currentTimeMillis() + time); // + 1일
        when(claims.get(eq(JwtUtil.USER_ID), eq(Long.class))).thenReturn(userId);
        when(claims.getSubject()).thenReturn(email);

        // 권한 조회 -> ROLE_USER
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        email, "pw", List.of(new SimpleGrantedAuthority(UserRoleEnum.USER.getAuthority()))
                ));
    }

    @Test
    @DisplayName("AT 재발급 + RT 회전(만료시간 임박)")
    void reissue() {
        // given
        setup(1000L * 60 * 60 * 24); // + 1일

        // AT/RT 생성 + 헤더/쿠키 세팅 (JwtUtil 은 mock이므로 호출만 검증)
        when(jwtUtil.createAccessToken(email, userId, UserRoleEnum.USER)).thenReturn("newAT");
        when(jwtUtil.createRefreshToken(email, userId)).thenReturn("newRT");

        HttpServletResponse response = new MockHttpServletResponse();

        // when
        ResReissueDtoV1 dto = authServiceV1.reissue(refreshToken, response);

        // then
        // AT 발급 & 셋팅
        verify(jwtUtil).createAccessToken(email, userId, UserRoleEnum.USER);
        verify(jwtUtil).addAccessTokenToHeader(response, "newAT");

        // RT 발급 & 셋팅
        verify(jwtUtil).createRefreshToken(email, userId);
        verify(jwtUtil).addRefreshTokenToCookie(response, "newRT");

        // validationToken & claims 조회 확인
        verify(jwtUtil).validateToken(refreshToken, false);
        verify(jwtUtil).getUserInfoFromToken(refreshToken);

        assertThat(dto.isRefreshRotated()).isTrue();
    }

    @Test
    @DisplayName("AT만 재발급 (RT 여유시간 충분)")
    void reissue_noRefreshToken() {
        // given
        setup(1000L * 60 * 60 * 24 * 4); // + 4일

        // AT 생성
        when(jwtUtil.createAccessToken(email, userId, UserRoleEnum.USER)).thenReturn("newAT");

        HttpServletResponse response = new MockHttpServletResponse();

        // when
        ResReissueDtoV1 dto = authServiceV1.reissue(refreshToken, response);

        // then
        // AT 발급 & 셋팅
        verify(jwtUtil).addAccessTokenToHeader(response, "newAT");
        verify(jwtUtil, never()).createRefreshToken(anyString(), anyLong());
        verify(jwtUtil, never()).addRefreshTokenToCookie(any(), anyString());

        assertThat(dto.isRefreshRotated()).isFalse();
    }

    @Test
    @DisplayName("refreshToken 없는 경우")
    void reissue_missingRefreshToken() {
        // null
        assertThatThrownBy(() -> authServiceV1.reissue(null, new MockHttpServletResponse()))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.AUTH_NO_REFRESH_TOKEN);

        // blank
        assertThatThrownBy(() -> authServiceV1.reissue("   ", new MockHttpServletResponse()))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.AUTH_NO_REFRESH_TOKEN);

        // JwtUtil 호출 자체가 없어야 정상
        verifyNoInteractions(jwtUtil, userDetailsService);
    }
}