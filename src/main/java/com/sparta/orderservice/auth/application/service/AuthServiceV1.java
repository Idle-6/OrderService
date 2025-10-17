package com.sparta.orderservice.auth.application.service;

import com.sparta.orderservice.auth.domain.entity.Auth;
import com.sparta.orderservice.auth.domain.repository.AuthRepository;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.advice.AuthErrorCode;
import com.sparta.orderservice.auth.presentation.advice.AuthException;
import com.sparta.orderservice.auth.presentation.dto.ResReissueDtoV1;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.global.presentation.advice.handler.GlobalExceptionHandler;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "auth Service")
public class AuthServiceV1 {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    private final JavaMailSender mailSender;
    private final AuthRepository authRepository;
    private int authNumber;

    // RT 남은 수명이 이 값보다 짧으면 회전
    @Value("${jwt.rt-rotate-time}")
    private long RT_ROTATE_TIME;


    public ResReissueDtoV1 reissue(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new AuthException(AuthErrorCode.AUTH_NO_REFRESH_TOKEN);

        jwtUtil.validateToken(refreshToken, false);

        Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
        long expTime = info.getExpiration().getTime();
        long userId = info.get(JwtUtil.USER_ID, Long.class);
        String email = info.getSubject();

        // 사용자 권한 조회
        UserDetails ud = userDetailsService.loadUserByUsername(email);
        UserRoleEnum role = ud.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority)
                .map(UserRoleEnum::fromAuthority).orElse(UserRoleEnum.USER);

        // AT 발급
        String newAT = jwtUtil.createAccessToken(email, userId, role);
        jwtUtil.addAccessTokenToHeader(response, newAT);

        long now = System.currentTimeMillis();
        boolean rotateRT = (expTime - now) <= RT_ROTATE_TIME;
        if(rotateRT) {
            String newRT = jwtUtil.createRefreshToken(email, userId);
            jwtUtil.addRefreshTokenToCookie(response, newRT);
        }

        // 기존 토큰 무효화
        userRepository.updateTokenExpiredAtById(info.get(JwtUtil.USER_ID, Long.class), System.currentTimeMillis());

        ResReissueDtoV1 dto = new ResReissueDtoV1();
        dto.setRefreshRotated(rotateRT);

        return dto;
    }

    public void makeRandomNumber() {
        SecureRandom r = new SecureRandom();
        authNumber = r.nextInt(900000)+100000;
    }

    @Transactional
    public void mailSender(String email) {
        makeRandomNumber();

        String tokenHash = hashAuthNumber(authNumber);

        // 기존 이메일 인증 내역 삭제
        authRepository.deleteByEmail(email);

        Auth auth = Auth.builder()
                .email(email)
                .tokenHash(tokenHash)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        authRepository.save(auth);


        String setForm = "noreply@orderservice.com";
        String toMail = email;
        String title = "[OrderService] 회원가입 인증 이메일입니다.";
        String content = "이메일 인증 코드 : " +
                        "<br><br>" +
                        "<tr>" +
                            "<td class='code' style='font-size:28px;line-height:1.3;font-weight:700;letter-spacing:2px;color:#111;padding:8px 0 0;'>" +
                            authNumber +
                            "</td>" +
                        "이 인증 코드는 15분 동안 유효합니다. <br>" +
                        "인증 시간이 초과된 경우, 인증 메일 재발송을 통해 회원가입을 완료해주세요." +
                        "</tr>";

                        
        mailSend(setForm, toMail, title, content);
    }

    private void mailSend(String setForm, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setFrom(new InternetAddress(setForm, "Order Service"));
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new AuthException(AuthErrorCode.AUTH_EMAIL_SEND_FAILED);
        } catch (UnsupportedEncodingException e) {
            throw new AuthException(AuthErrorCode.AUTH_EMAIL_ENCODING_FAILED);
        }
    }


    // SHA-256으로 인증번호 해싱 (DB 저장용)
    private String hashAuthNumber(int number) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(String.valueOf(number).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.AUTH_HASH_FAILED);
        }
    }

    @Transactional
    public Auth verifyEmail(String email, int token) {
        Auth auth = authRepository.findTopByEmailAndConsumedAtIsNull(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_NO_VERIFICATION));

        if(auth.isExpired()) {
            throw new AuthException(AuthErrorCode.AUTH_EXPIRED_VERIFICATION);
        }

        if(auth.isConsumed()) {
            throw new AuthException(AuthErrorCode.AUTH_USED_VERIFICATION);
        }

        String inputHash = hashAuthNumber(token);
        if(!inputHash.equals(auth.getTokenHash())) {
            throw new AuthException(AuthErrorCode.AUTH_VERIFICATION_MISMATCH);
        }

        auth.updateConsumedAt(LocalDateTime.now());
        return authRepository.save(auth);
    }
}
