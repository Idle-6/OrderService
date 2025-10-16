package com.sparta.orderservice.auth.presentation.controller;

import com.sparta.orderservice.auth.application.service.AuthServiceV1;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.dto.ReqEmailCheckDtoV1;
import com.sparta.orderservice.auth.presentation.dto.ResEmailCheckDtoV1;
import com.sparta.orderservice.auth.presentation.dto.ResEmailSendDtoV1;
import com.sparta.orderservice.auth.presentation.dto.ResReissueDtoV1;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthControllerV1 {

    private final AuthServiceV1 authService;

    @PostMapping("/reissue")
    public ResponseEntity<ResReissueDtoV1> reissue (@CookieValue(value = JwtUtil.REFRESH_COOKIE_NAME) String rtFromCookie, HttpServletResponse response) {
        String refreshToken = rtFromCookie;
        ResReissueDtoV1 body = authService.reissue(refreshToken, response);
        body.setMessage("로그인 시간이 연장되었습니다.");

        return ResponseEntity.ok(body);
    }

    @GetMapping("/email/varification")
    public ResponseEntity<ResEmailSendDtoV1> mailSend(@Email @NotEmpty(message = "이메일을 입력해 주세요") String email){
        authService.mailSender(email);

        ResEmailSendDtoV1 body = new ResEmailSendDtoV1(
            true
                ,"메일이 성공적으로 발송되었습니다."
        );

        return ResponseEntity.ok(body);
    }

    @PostMapping("/email/varification")
    public ResponseEntity<ResEmailCheckDtoV1> verifyEmail(@RequestBody @Valid ReqEmailCheckDtoV1 req){
        ResEmailCheckDtoV1 body = new ResEmailCheckDtoV1(
                true
                , "이메일 인증이 완료되었습니다."
                , req.getEmail()
        );

        return ResponseEntity.ok(body);
    }
}
