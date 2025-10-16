package com.sparta.orderservice.auth.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResReissueDtoV1 {
    private boolean refreshRotated;   // RT 회전 여부
    private String message;
}
