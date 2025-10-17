package com.sparta.orderservice.auth.application.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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
        Random r = new Random();
        authNumber = r.nextInt(900000)+100000;
    }

    public void mailSender(String email) {
        makeRandomNumber();
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
            throw new RuntimeException("이메일 전송에 실패했습니다.");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
