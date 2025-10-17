package com.sparta.orderservice.payment.presentation.dto.request;

import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPaymentUpdateDtoV1 {

    private PaymentStatusEnum status;

}
