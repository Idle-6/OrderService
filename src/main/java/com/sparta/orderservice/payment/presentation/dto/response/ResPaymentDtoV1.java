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
public class ResPaymentDtoV1 {

    private UUID paymentId;

    private UUID orderId;

    private String orderName; // 주문 상품명 또는 내역

    private BigDecimal amount;

    private String payType; // 결제 방식 (카드, 계좌이체 등)

    private String paymentKey; // PG 결제 키(외부 결제사 연동시)

    private String userName;

    private LocalDateTime paidAt;

    private String status;

    private String receiptUrl; // 영수증 또는 결제 내역 URL
}

