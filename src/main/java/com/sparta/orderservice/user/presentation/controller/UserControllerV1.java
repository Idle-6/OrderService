package com.sparta.orderservice.user.presentation.controller;

import com.sparta.orderservice.user.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserControllerV1 {

    @PostMapping("/sign-up")
    public ResponseEntity<ResUserDtoV1> signUp(@Valid ReqSignupDtoV1 requestDto, BindingResult bindingResuilt){
        ResUserDtoV1 res = new ResUserDtoV1(requestDto.getEmail(), requestDto.getName(), requestDto.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ResUserUpdateDtoV1> updateUser(@PathVariable Long userId, ReqUserUpdateDtoV1 req){
        ResUserUpdateDtoV1 body = new ResUserUpdateDtoV1(
                userId,
                req.getName() != null ? req.getName() : "홍길동",
                "회원 정보가 수정되었습니다."
        );
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResUserDeleteDtoV1> deleteUser(@PathVariable Long userId){
        ResUserDeleteDtoV1 body = new ResUserDeleteDtoV1(
                userId,
                true,
                "회원이 정상적으로 삭제되었습니다."
        );
        return ResponseEntity.ok(body);
    }
}
