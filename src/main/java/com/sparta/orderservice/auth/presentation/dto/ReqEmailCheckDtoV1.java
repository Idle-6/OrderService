package com.sparta.orderservice.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqEmailCheckDtoV1 {
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;
    @NotNull(message = "인증 번호를 입력해 주세요")
    private int token;
}
