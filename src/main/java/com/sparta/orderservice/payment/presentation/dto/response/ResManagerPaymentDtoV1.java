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
public class ResManagerPaymentDtoV1 {

    private UUID paymentId;

    private Integer amount;

    private PaymentStatusEnum status;

    private PaymentMethodEnum payType;

    private LocalDateTime paidAt;

    private String userName;

    private String storeName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @QueryProjection
    public ResManagerPaymentDtoV1(UUID paymentId, Integer amount, PaymentStatusEnum status, LocalDateTime paidAt, PaymentMethodEnum payType, String userName, String storeName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.payType = payType;
        this.paidAt = paidAt;
        this.userName = userName;
        this.storeName = storeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
