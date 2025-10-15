package com.sparta.orderservice.auth.infrastructure.util;

import com.sparta.orderservice.auth.presentation.advice.AuthErrorCode;
import com.sparta.orderservice.auth.presentation.advice.AuthException;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.presentation.advice.UserException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // === 공통 상수 ===
    public static final String AUTHORIZATION_HEADER = "Authorization";  // AT 헤더
    public static final String AUTHORIZATION_KEY = "auth";              // 사용자 권한 값의 KEY
    public static final String BEARER_PREFIX = "Bearer ";               // Token 식별자
    public static final String REFRESH_COOKIE_NAME = "refresh_token";   // RT 쿠키명

    public static final String TOKEN_TYPE_KEY = "token_type";      // 토큰 종류 식별 클레임
    public static final String TOKEN_TYPE_ACCESS = "AT";    // accessToken
    public static final String TOKEN_TYPE_REFRESH = "RT";   // refreshToken

    public static final String USER_ID = "user_id";

    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // === 설정 값 ===
    @Autowired
    private JwtProperties jwtProperties;

//    @Value("${jwt.cookie.secure:true}")       // HTTPS 환경이면 true
//    private boolean cookieSecure;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        // Base64 디코딩 후 키 생성
        byte[] bytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        key = Keys.hmacShaKeyFor(bytes);
    }

    // === 토큰 발급 ===
    /** Access Token 발급 (prefix, 권한 포함) */
    public String createAccessToken(String username, Long userId, UserRoleEnum role) {
        return BEARER_PREFIX + buildToken(username, userId, role, TOKEN_TYPE_ACCESS, jwtProperties.getAccessTokenTime());
    }

    /** Refresh Token 발급 (권한 불필요, typ=RT) */
    public String createRefreshToken(String username, Long userId) {
        return buildToken(username, userId, null, TOKEN_TYPE_REFRESH, jwtProperties.getRefreshTokenTime());
    }

    /** 토큰 생성 */
    private String buildToken(String username, Long userId, UserRoleEnum role, String tokenType, long tokenTime) {
        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)       // 사용자 식별자값(ID)
                .setIssuedAt(now)           // 발급일
                .setExpiration(new Date(now.getTime() + tokenTime))  // 만료 기한
                .claim(TOKEN_TYPE_KEY, tokenType)   // AT/RT 구분
                .claim(USER_ID, userId)
                .signWith(key, signatureAlgorithm); // 암호화 알고리즘

            if (role != null) {
                builder.claim(AUTHORIZATION_KEY, role.getAuthority()); // 사용자 권한
            }

        return builder.compact();   // signed jwt 생성
    }

    // === 헤더/쿠키 헬퍼 ===
    /** RT를 HttpOnly 쿠키에 저장 */
    public void addRefreshTokenToCookie(HttpServletResponse res, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(JwtUtil.REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false) // HTTP
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenTime())
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** AT를 헤더에 저장 */
    public void addAccessTokenToHeader(HttpServletResponse res, String accessToken) {
        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
    }

    /** RT 쿠키 만료 */
    public void expireRefreshCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
    }

    public String getAccessTokenFromHeader(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        return substringToken(bearerToken);
    }

    /** JWT 토큰 substring */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        return null;
    }

    /** 토큰 검증 */
    public boolean validateToken(String token, boolean requireAccess) {
        try {
            Jws<Claims> jws = parse(token);
            Claims claims = jws.getBody();

            String typ = claims.get(TOKEN_TYPE_KEY, String.class);
            if (requireAccess && !TOKEN_TYPE_ACCESS.equals(typ)) {
                logger.error(AuthErrorCode.AUTH_TOKEN_TYPE_MISMATCH.getErrorMessage());
                throw new AuthException(AuthErrorCode.AUTH_TOKEN_TYPE_MISMATCH);
            }

            if (!requireAccess && !TOKEN_TYPE_REFRESH.equals(typ)) {
                logger.error(AuthErrorCode.AUTH_TOKEN_TYPE_MISMATCH.getErrorMessage());
                throw new AuthException(AuthErrorCode.AUTH_TOKEN_TYPE_MISMATCH);
            }
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error(AuthErrorCode.AUTH_INVALID_JWT_SIGNATURE.getErrorMessage());
            throw new AuthException(AuthErrorCode.AUTH_INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            logger.error(AuthErrorCode.AUTH_EXPIRED_TOKEN.getErrorMessage());
            throw new AuthException(AuthErrorCode.AUTH_EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            logger.error(AuthErrorCode.AUTH_UNSUPPORTED_JWT_TOKEN.getErrorMessage());
            throw new AuthException(AuthErrorCode.AUTH_UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            logger.error(AuthErrorCode.AUTH_INVALID_CLAIMS.getErrorMessage());
            throw new AuthException(AuthErrorCode.AUTH_INVALID_CLAIMS);
        }
    }

    /** 파싱 */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    /** 토큰에서 사용자 정보 가져오기 */
    public Claims getUserInfoFromToken(String token) {
        return parse(token).getBody();
    }

    public long getExpiredTimeFromHeader(HttpServletRequest request) {
        String accessToken = getAccessTokenFromHeader(request);
        Claims info = parse(accessToken).getBody();
        return info.getExpiration().getTime();
    }
}
