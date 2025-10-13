package com.sparta.orderservice.auth.presentation.controller;

import com.sparta.orderservice.auth.application.service.AuthServiceV1;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Q. 이메일 인증 사용자 정보는 어디에 저장해두는지?
    // DB에 굳이 저장할 필요 없지 않나?
    @GetMapping("/email/varification")
    public ResponseEntity<ResEmailVarificatoinSendDtoV1> sendVerification(String email){
        ResEmailVarificatoinSendDtoV1 body = new ResEmailVarificatoinSendDtoV1(
            true
                ,"메일이 성공적으로 발송되었습니다."
        );

        return ResponseEntity.ok(body);
    }

    @PostMapping("/email/varification")
    public ResponseEntity<ResEmailVarificationCheckDtoV1> verifyEmail(@RequestBody @Valid ReqEmailVarcxificationCheckDtoV1 req){
        ResEmailVarificationCheckDtoV1 body = new ResEmailVarificationCheckDtoV1(
                true
                , "이메일 인증이 완료되었습니다."
                , req.getEmail()
        );

        return ResponseEntity.ok(body);
    }
}
