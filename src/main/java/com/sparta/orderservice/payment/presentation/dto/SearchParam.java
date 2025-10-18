package com.sparta.orderservice.payment.presentation.dto;

import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam {

    private String term;

}
