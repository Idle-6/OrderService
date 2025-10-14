package com.sparta.orderservice.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPasswordUpdateDtoV1 {
    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
}
