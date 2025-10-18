package com.sparta.orderservice.manage.presentation.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUserDtoV1 {
    private String userId;
    private String name;
    private String email;
    private String address;
    private String status;
}
