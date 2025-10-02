package com.sparta.orderservice.auth.presentation.dto;

import com.sparta.orderservice.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResLoginDtoV1 {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private UserEntity user;
    private int expiresIn;       // access 만료 기한
}