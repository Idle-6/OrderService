package com.sparta.orderservice.manage.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCustomerDto {
    private String userId;
    private String name;
    private String email;
    private String address;
    private String status;
}
