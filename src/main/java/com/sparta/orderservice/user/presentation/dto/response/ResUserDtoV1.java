package com.sparta.orderservice.user.presentation.dto.response;

import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDtoV1 {
    private String email;
    private String name;
    private UserRoleEnum role;
}
