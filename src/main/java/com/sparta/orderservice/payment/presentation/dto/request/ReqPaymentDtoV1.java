package com.sparta.orderservice.payment.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPaymentDtoV1 {

    private UUID orderId;

    private Long userId;

    private BigDecimal amount;

    private String payType;

    private String cardNumber;   // 카드 번호(카드 결제시)

    private String cardExpiry;   // 카드 유효기간

    private String cardCvc;      // 카드 CVC

    private String pgToken;     // 결제사에서 보낸 Token

}