package com.sparta.orderservice.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserUpdateDtoV1 {
    private Long id;
    private String name;
    private String message;
}
