package com.sparta.orderservice.user.presentation.dto.request;

import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSignupDtoV1 {
    @Email
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{8,15}$",
            message = "비밀번호는 8자 이상 15자 이하이며, 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address;

    private String role = UserRoleEnum.USER.getAuthority();

    private boolean admin = false;
    private String adminToken = "";


}
