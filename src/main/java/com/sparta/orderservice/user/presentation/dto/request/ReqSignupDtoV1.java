package com.sparta.orderservice.user.presentation.dto.request;

import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSignupDtoV1 {
    @Email
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address;

    private String role = UserRoleEnum.USER.getAuthority();

    private boolean admin = false;
    private String adminToken = "";


}
