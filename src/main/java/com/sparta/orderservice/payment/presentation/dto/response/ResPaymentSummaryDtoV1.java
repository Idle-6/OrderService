package com.sparta.orderservice.payment.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResPaymentSummaryDtoV1 {

    private UUID paymentId;

    private Integer amount;

    private PaymentStatusEnum status;

    private LocalDateTime paidAt;

    @QueryProjection
    public ResPaymentSummaryDtoV1(UUID paymentId, Integer amount, PaymentStatusEnum status, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }
}