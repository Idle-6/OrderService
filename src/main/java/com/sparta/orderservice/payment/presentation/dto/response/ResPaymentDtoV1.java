package com.sparta.orderservice.payment.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResPaymentDtoV1 {

    private UUID paymentId;

    private UUID orderId;

    private BigDecimal amount;

    private String payType; // 결제 방식 (카드, 계좌이체 등)

    private String userName;

    private String status;

    private LocalDateTime paidAt;

    private LocalDateTime updatedAt;

    private LocalDateTime canceledAt;

    @QueryProjection
    public ResPaymentDtoV1(UUID paymentId, UUID orderId, BigDecimal amount, PaymentMethodEnum payType, String userName, PaymentStatusEnum status, LocalDateTime paidAt, LocalDateTime canceledAt, LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.payType = payType.getDisplayName();
        this.userName = userName;
        this.status = status.getDescription();
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.updatedAt = updatedAt;
    }
}


