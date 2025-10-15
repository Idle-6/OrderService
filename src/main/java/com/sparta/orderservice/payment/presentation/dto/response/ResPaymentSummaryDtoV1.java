package com.sparta.orderservice.payment.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
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

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidAt;

    @QueryProjection
    public ResPaymentSummaryDtoV1(UUID paymentId, BigDecimal amount, PaymentStatusEnum status, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status.getDescription();
        this.paidAt = paidAt;
    }
}
