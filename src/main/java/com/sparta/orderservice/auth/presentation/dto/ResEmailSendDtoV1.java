package com.sparta.orderservice.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResEmailSendDtoV1 {
    private boolean success;
    private String message;
}
