package com.sparta.orderservice.auth.infrastructure.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public static final String TOKEN_TYPE_KEY = "typ";      // 토큰 종류 식별 클레임
    public static final String TOKEN_TYPE_ACCESS = "AT";    // accessToken
    public static final String TOKEN_TYPE_REFRESH = "RT";   // refreshToken

    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // === 설정 값 ===
    // Q. 시크릿키 저장 위치?
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    @Value("${jwt.access-token-time:1800000}")     // 30분
    private long accessTokenTime;
    @Value("${jwt.refresh-token-time:1209600000}")// 14일 기본
    private long refreshTokenTime;

    // Q. HTTPS 적용할건지?
//    @Value("${jwt.cookie.secure:true}")       // HTTPS 환경이면 true
//    private boolean cookieSecure;
//    @Value("${jwt.cookie.same-site:Lax}")     // None/Lax/Strict
//    private String cookieSameSite;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        // Base64 디코딩 후 키 생성
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // === 토큰 발급 ===

    /**
     * Access Token 발급 (권한 포함)
     *
     * @param username
     * @param role
     * @return
     */
    public String createAccessToken(String username, String role) {
        return BEARER_PREFIX + buildToken(username, role, TOKEN_TYPE_ACCESS, accessTokenTime);
    }

    /**
     * Refresh Token 발급 (권한 불필요, typ=RT)
     * refresh는 최소 정보 + Type=RT만
     * @param username
     * @return
     */
    public String createRefreshToken(String username) {
        // Q. 왜 refreshToken에는 Bearer을 붙이지 않는가?
        // API 호출 시 직접 쓰는 토큰이 아니기 때문에 Bearer 필요 없음
        return buildToken(username, null, TOKEN_TYPE_REFRESH, refreshTokenTime);
    }

    // 토큰 생성
    private String buildToken(String username, String role, String tokenType, long tokenTime) {
        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)       // 사용자 식별자값(ID)
                .setIssuedAt(now)           // 발급일
                .setExpiration(new Date(now.getTime() + tokenTime))  // 만료 기한
                .claim(TOKEN_TYPE_KEY, tokenType)   // AT/RT 구분
                .signWith(key, signatureAlgorithm); // 암호화 알고리즘

            if (role != null) {
                builder.claim(AUTHORIZATION_KEY, role); // 사용자 권한
            }

        return builder.compact();   // signed jwt 생성
    }

    // === 헤더/쿠키 헬퍼 ===

    /**
     * AT를 JWT Cookie 에 저장
     * @param accessToken
     * @param res
     */
    public void addAccessTokenToCookie(String accessToken, HttpServletResponse res) {

        try {
            res.addCookie(buildCookie(TOKEN_TYPE_ACCESS, accessToken));
        } catch (UnsupportedEncodingException e) {
            logger.error("쿠키 인코딩 실패: {}", e.getMessage());
        }
    }

    /**
     * RT를 HttpOnly 쿠키에 저장
     * @param refreshToken
     * @param res
     */
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse res) {
        try {
            res.addCookie(buildCookie(TOKEN_TYPE_REFRESH, refreshToken));
        } catch (UnsupportedEncodingException e) {
            logger.error("쿠키 인코딩 실패: {}", e.getMessage());
        }
    }

    private Cookie buildCookie(String tokenType, String token) throws UnsupportedEncodingException {
        token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행
        Cookie cookie = null;

        if(tokenType == TOKEN_TYPE_ACCESS) {
            // Q. accessToken는 주로 헤더에 두나요?
            cookie = new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setPath("/");
        } else if(tokenType == TOKEN_TYPE_REFRESH){
            cookie = new Cookie(REFRESH_COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/api/auth"); // /api/auth 경로와 그 하위 경로 요청에만 쿠키 전송
//            cookie.setPath("/");
        }

        return cookie;
    }

    /**
     * RT 쿠키 제거(로그아웃)
     * @param res
     */
    public void expireRefreshCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "/api/auth");
        res.addCookie(cookie);
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증
    public boolean validateToken(String token, boolean requireAccess) {
        try {
            Jws<Claims> jws = parse(token);
            Claims claims = jws.getBody();

            String typ = claims.get(TOKEN_TYPE_KEY, String.class);
            if (requireAccess && !TOKEN_TYPE_ACCESS.equals(typ)) {
                logger.error("Access 토큰이 아님");
                return false;
            }

            if (!requireAccess && !TOKEN_TYPE_REFRESH.equals(typ)) {
                logger.error("Refresh 토큰이 아님");
                return false;
            }
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
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
}
