package com.sparta.orderservice.auth;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("accesstoken 생성 검증")
    void accessTokenCreateTest() {
        // given
        String username = "testUser";
        UserRoleEnum role = UserRoleEnum.USER;

        // when
        String accessToken = jwtUtil.createAccessToken(username, role);
        String tokenValue = jwtUtil.substringToken(accessToken);

        // then
        assertThat(jwtUtil.validateToken(tokenValue, true)).isTrue();

        Claims claims = jwtUtil.getUserInfoFromToken(tokenValue);
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("auth", String.class)).isEqualTo(role);
        assertThat(claims.get("typ", String.class)).isEqualTo("AT");
    }

    @Test
    @DisplayName("refreshToken 생성 검증")
    void refreshTokenCreateTest() {
        // given
        String username = "testUser";

        // when
        String refreshToken = jwtUtil.createRefreshToken(username);

        // then
        assertThat(jwtUtil.validateToken(refreshToken, false)).isTrue();

        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("typ", String.class)).isEqualTo("RT");
        assertThat(claims.get("auth")).isNull();
    }

    @Test
    @DisplayName("만료된 토큰 검증")
    void expiredTokenTest() throws InterruptedException {
        // given
        String username = "expiredUser";
        UserRoleEnum role = UserRoleEnum.USER;

        String accessToken = jwtUtil.createAccessToken(username, role);
        String tokenValue = jwtUtil.substringToken(accessToken);

        // when (1초 뒤 만료되도록 yml 설정했으니 sleep)
        Thread.sleep(1500);

        // then
        boolean result = jwtUtil.validateToken(tokenValue, true);
        assertThat(result).isFalse();
    }
}
