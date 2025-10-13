package com.sparta.orderservice.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDeleteDtoV1 {
    private Long id;
    private boolean success;
    private String message;
}
