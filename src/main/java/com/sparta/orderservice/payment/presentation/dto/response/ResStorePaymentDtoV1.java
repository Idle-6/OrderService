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
public class ResStorePaymentDtoV1 {

    private UUID paymentId;

    private Integer amount;

    private PaymentStatusEnum status;

    private LocalDateTime paidAt;

    private PaymentMethodEnum payType;

    private String userName;

    private LocalDateTime createdAt;

    @QueryProjection
    public ResStorePaymentDtoV1(UUID paymentId, Integer amount, PaymentStatusEnum status, LocalDateTime paidAt, PaymentMethodEnum payType, String userName, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
        this.payType = payType;
        this.userName = userName;
        this.createdAt = createdAt;
    }
}
