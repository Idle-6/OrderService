package com.sparta.orderservice.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqEmailVarcxificationCheckDtoV1 {
    private String email;
    private String token;
}
