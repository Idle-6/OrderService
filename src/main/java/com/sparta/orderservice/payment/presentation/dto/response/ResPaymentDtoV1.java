package com.sparta.orderservice.payment.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResPaymentDtoV1 {

    private UUID paymentId;

    private UUID orderId;

    private Integer amount;

    private PaymentMethodEnum payType; // 결제 방식 (카드, 계좌이체 등)

    private String userName;

    private PaymentStatusEnum status;

    private LocalDateTime paidAt;

    private LocalDateTime updatedAt;

    private LocalDateTime canceledAt;

    @QueryProjection
    public ResPaymentDtoV1(UUID paymentId, UUID orderId, Integer amount, PaymentMethodEnum payType, String userName, PaymentStatusEnum status, LocalDateTime paidAt, LocalDateTime canceledAt, LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.payType = payType;
        this.userName = userName;
        this.status = status;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.updatedAt = updatedAt;
    }
}


