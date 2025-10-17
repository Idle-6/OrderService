package com.sparta.orderservice.auth.infrastructure.util;

import com.sparta.orderservice.auth.presentation.advice.AuthException;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;


class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtProperties props;

    private String base64Key;

    @BeforeEach
    void setUp() {
        // 32바이트(256bit) 이상 키를 Base64로
        String rawKey = "01234567890123456789012345678901"; // 32 bytes
        base64Key = Base64.getEncoder().encodeToString(rawKey.getBytes(StandardCharsets.UTF_8));

        props = Mockito.mock(JwtProperties.class);
        when(props.getSecretKey()).thenReturn(base64Key);
        when(props.getAccessTokenTime()).thenReturn(30 * 60 * 1000L);        // 30분 (ms)
        when(props.getRefreshTokenTime()).thenReturn(14L * 24 * 60 * 60 * 1000); // 14일 (ms)

        jwtUtil = new JwtUtil();
        // @Autowired 필드 주입 대체
        ReflectionTestUtils.setField(jwtUtil, "jwtProperties", props);
        // @PostConstruct 대체
        jwtUtil.init();
    }

    @Nested
    @DisplayName("토큰 발급/파싱")
    class IssueAndParse {

        @Test
        @DisplayName("AT 발급: Bearer 프리픽스 포함 + 주요 클레임 확인")
        void createAccessToken() {
            String accessToken = jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER);
            assertThat(accessToken).startsWith(JwtUtil.BEARER_PREFIX);

            String at = jwtUtil.substringToken(accessToken);
            Claims claims = jwtUtil.getUserInfoFromToken(at);

            assertThat(claims.getSubject()).isEqualTo("tester");
            assertThat(claims.get(JwtUtil.USER_ID, Integer.class)).isEqualTo(1);
            assertThat(claims.get(JwtUtil.TOKEN_TYPE_KEY, String.class)).isEqualTo(JwtUtil.TOKEN_TYPE_ACCESS);
            assertThat(claims.get(JwtUtil.AUTHORIZATION_KEY, String.class)).isEqualTo(UserRoleEnum.USER.getAuthority());
            assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        }

        @Test
        @DisplayName("RT 발급: 권한 없이 typ=RT")
        void createRefreshToken() {
            String rt = jwtUtil.createRefreshToken("tester", 1L);
            // RT는 Bearer 프리픽스 없음
            Claims claims = jwtUtil.getUserInfoFromToken(rt);
            assertThat(claims.get(JwtUtil.TOKEN_TYPE_KEY, String.class)).isEqualTo(JwtUtil.TOKEN_TYPE_REFRESH);
            assertThat(claims.get(JwtUtil.AUTHORIZATION_KEY)).isNull();
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class Validate {

        @Test
        @DisplayName("올바른 AT면 requireAccess=true에서 통과")
        void validateAccessToken_ok() {
            String at = jwtUtil.substringToken(jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER));
            boolean ok = jwtUtil.validateToken(at, true);
            assertThat(ok).isTrue();
        }

        @Test
        @DisplayName("AT를 requireAccess=false로 검증하면 타입 불일치 예외")
        void validateAccessToken_asRefresh_fails() {
            String at = jwtUtil.substringToken(jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER));
            assertThatThrownBy(() -> jwtUtil.validateToken(at, false))
                    .isInstanceOf(AuthException.class);
        }

        @Test
        @DisplayName("RT를 requireAccess=true로 검증하면 타입 불일치 예외")
        void validateRefreshToken_asAccess_fails() {
            String rt = jwtUtil.createRefreshToken("tester", 1L);
            assertThatThrownBy(() -> jwtUtil.validateToken(rt, true))
                    .isInstanceOf(AuthException.class);
        }

        @Test
        @DisplayName("만료된 토큰 검증 실패")
        void validateTomen_expired_fails() {
            // 만료를 유도하기 위해 refreshTokenTime을 음수로 지정 → 새 토큰 발급
            when(props.getRefreshTokenTime()).thenReturn(-1000L);
            String expiredRt = jwtUtil.createRefreshToken("tester", 1L);

            assertThatThrownBy(() -> jwtUtil.validateToken(expiredRt, false))
                    .isInstanceOf(AuthException.class);
        }
    }

    @Nested
    @DisplayName("헤더/쿠키 헬퍼")
    class HeaderCookie {

        @Test
        @DisplayName("AT를 헤더에 담는다")
        void addAccessTokenToHeader() {
            MockHttpServletResponse res = new MockHttpServletResponse();
            String atWithBearer = jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER);
            jwtUtil.addAccessTokenToHeader(res, atWithBearer);

            assertThat(res.getHeader(JwtUtil.AUTHORIZATION_HEADER)).isEqualTo(atWithBearer);
        }

        @Test
        @DisplayName("RT를 HttpOnly 쿠키(secure=false, path=/, Max-Age 존재)로 세팅")
        void addRefreshTokenToCookie() {
            MockHttpServletResponse res = new MockHttpServletResponse();
            String rt = jwtUtil.createRefreshToken("tester", 1L);

            jwtUtil.addRefreshTokenToCookie(res, rt);

            String setCookie = res.getHeader("Set-Cookie");
            assertThat(setCookie).contains(JwtUtil.REFRESH_COOKIE_NAME + "=" + rt);
            assertThat(setCookie).contains("HttpOnly");
            assertThat(setCookie).contains("Path=/");
            assertThat(setCookie).contains("Max-Age=");
        }

        @Test
        @DisplayName("RT 쿠키 만료")
        void expireRefreshCookie() {
            MockHttpServletResponse res = new MockHttpServletResponse();
            jwtUtil.expireRefreshCookie(res);

            Cookie[] cookies = res.getCookies();
            assertThat(cookies).isNotNull();
            assertThat(cookies).anySatisfy(c -> {
                assertThat(c.getName()).isEqualTo(JwtUtil.REFRESH_COOKIE_NAME);
                assertThat(c.getMaxAge()).isZero();
                assertThat(c.getPath()).isEqualTo("/");
            });
        }

        @Test
        @DisplayName("Authorization 헤더에서 Bearer 제거 후 토큰 얻기")
        void getAccessTokenFromHeader() {
            String atWithBearer = jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER);

            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader(JwtUtil.AUTHORIZATION_HEADER, atWithBearer);

            String got = jwtUtil.getAccessTokenFromHeader(req);
            assertThat(got).isEqualTo(jwtUtil.substringToken(atWithBearer));
        }

        @Test
        @DisplayName("Bearer 프리픽스 없으면 substringToken은 null")
        void substringToken_nullCases() {
            assertThat(jwtUtil.substringToken(null)).isNull();
            assertThat(jwtUtil.substringToken("")).isNull();
            assertThat(jwtUtil.substringToken("NotBearer token")).isNull();
        }

        @Test
        @DisplayName("헤더 AT에서 만료 시각(ms) 추출")
        void getExpiredTimeFromHeader() {
            String atWithBearer = jwtUtil.createAccessToken("tester", 1L, UserRoleEnum.USER);
            String at = jwtUtil.substringToken(atWithBearer);
            Claims claims = jwtUtil.getUserInfoFromToken(at);

            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader(JwtUtil.AUTHORIZATION_HEADER, atWithBearer);

            long expFromMethod = jwtUtil.getExpiredTimeFromHeader(req);
            assertThat(expFromMethod).isEqualTo(claims.getExpiration().getTime());
        }
    }
}