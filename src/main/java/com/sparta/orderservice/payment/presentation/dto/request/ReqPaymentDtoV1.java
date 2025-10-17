package com.sparta.orderservice.payment.presentation.dto.request;

import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPaymentDtoV1 {

    private UUID orderId;

    @NotNull(message = "결제 금액은 필수입니다.")
    private Integer amount;

    @NotNull(message = "결제 수단은 필수입니다.")
    private PaymentMethodEnum payType;

    private String pgToken;     // 결제사에서 받은 Token

}