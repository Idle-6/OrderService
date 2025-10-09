package com.sparta.orderservice.global.presentation.advice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResExceptionDtoV1 {

    private Integer errorCode;
    private String message;
}
