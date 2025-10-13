package com.sparta.orderservice.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDtoV1 {
    private String email;
    private String name;
    private String role;
}
