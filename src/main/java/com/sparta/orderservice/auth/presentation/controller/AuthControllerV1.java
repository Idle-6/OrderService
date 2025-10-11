package com.sparta.orderservice.auth.presentation.controller;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.presentation.dto.*;
import com.sparta.orderservice.user.domain.UserEntity;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthControllerV1 {

    private final JwtUtil jwtUtil;

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
