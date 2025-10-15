package com.sparta.orderservice.payment.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResPaymentDtoV1 {

    private UUID paymentId;

    private UUID orderId;

    private BigDecimal amount;

    private String payType; // 결제 방식 (카드, 계좌이체 등)

    private String userName;

    @Setter
    private String status;

    private LocalDateTime paidAt;

    private LocalDateTime updatedAt;

    private LocalDateTime canceledAt;

    @QueryProjection
    public ResPaymentDtoV1(UUID paymentId, UUID orderId, BigDecimal amount, String payType, String userName, String status, LocalDateTime paidAt, LocalDateTime canceledAt, LocalDateTime updatedAt) {
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


