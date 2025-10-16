package com.sparta.orderservice.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqEmailSendDtoV1 {
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;
}
