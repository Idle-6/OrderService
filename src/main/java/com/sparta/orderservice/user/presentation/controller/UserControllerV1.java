package com.sparta.orderservice.user.presentation.controller;

import com.sparta.orderservice.user.application.service.UserServiceV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.presentation.dto.request.ReqPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqSignupDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqUserUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.response.ResPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.response.ResUserDeleteDtoV1;
import com.sparta.orderservice.user.presentation.dto.response.ResUserDtoV1;
import com.sparta.orderservice.user.presentation.dto.response.ResUserUpdateDtoV1;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserControllerV1 {

    private final UserServiceV1 userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResUserDtoV1> signUp(@RequestBody @Valid ReqSignupDtoV1 requestDto){

        User user = userService.signUp(requestDto);

        ResUserDtoV1 res = new ResUserDtoV1(user.getEmail(), user.getName(), user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ResUserUpdateDtoV1> updateUser(@PathVariable Long userId, @RequestBody ReqUserUpdateDtoV1 requestDto){

        User user = userService.updateUser(userId, requestDto);

        ResUserUpdateDtoV1 body = new ResUserUpdateDtoV1(
                user.getUserId(),
                user.getName(),
                "회원 정보가 수정되었습니다."
        );
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ResPasswordUpdateDtoV1> updatePassword(@PathVariable Long userId, @RequestBody ReqPasswordUpdateDtoV1 requestDto){

        userService.updaterPassword(userId, requestDto);

        ResPasswordUpdateDtoV1 body = new ResPasswordUpdateDtoV1(
                userId,
                "비밀번호가 변경되었습니다."
        );
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResUserDeleteDtoV1> deleteUser(@PathVariable Long userId, HttpServletRequest request, HttpServletResponse response){

        userService.deleteUser(request, response, userId);

        ResUserDeleteDtoV1 body = new ResUserDeleteDtoV1(
                userId,
                true,
                "회원이 정상적으로 삭제되었습니다."
        );
        return ResponseEntity.ok(body);
    }
}
