package com.sparta.orderservice.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResPaymentSummaryDtoV1 {

    private UUID paymentId;

    private String orderName;

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidAt;
}
