package com.sparta.orderservice.user.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ReqUserUpdateDtoV1 {
    private String name;
    private String address;
}
