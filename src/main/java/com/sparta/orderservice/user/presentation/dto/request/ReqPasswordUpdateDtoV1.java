package com.sparta.orderservice.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPasswordUpdateDtoV1 {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{8,15}$",
            message = "비밀번호는 8자 이상 15자 이하이며, 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;
}
