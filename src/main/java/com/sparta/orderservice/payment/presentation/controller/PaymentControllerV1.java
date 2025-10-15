package com.sparta.orderservice.payment.presentation.controller;

import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
public class PaymentControllerV1 {

    @PostMapping
    public ResponseEntity<ResPaymentDtoV1> completePayment(@RequestBody @Valid ReqPaymentDtoV1 request) {
        ResPaymentDtoV1 response = new ResPaymentDtoV1(
                UUID.randomUUID(),
                request.getOrderId(),
                "주문 1",
                BigDecimal.valueOf(50000),
                PaymentMethodEnum.MOBILE_PAY.getDisplayName(),
                null,
                "홍길동",
                LocalDateTime.now(),
                PaymentStatusEnum.PAID.getDescription(),
                "https://example.com/paymentId/receipt"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResPaymentSummaryDtoV1>> getPaymentList(
            @PageableDefault(
                    size = 10,
                    sort = "paidAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        ResPaymentSummaryDtoV1 response = new ResPaymentSummaryDtoV1(
                UUID.randomUUID(),
                "주문 1",
                BigDecimal.valueOf(50000),
                PaymentStatusEnum.PAID.getDescription(),
                LocalDateTime.now()
        );

        List<ResPaymentSummaryDtoV1> payments = List.of(response);
        Page<ResPaymentSummaryDtoV1> paymentPage = new PageImpl<>(payments, pageable, 0);

        return ResponseEntity.ok(paymentPage);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResPaymentDtoV1> getPayment(@PathVariable(name = "paymentId") UUID paymentId) {
        ResPaymentDtoV1 response = new ResPaymentDtoV1(
                paymentId,
                UUID.randomUUID(),
                "주문 1",
                BigDecimal.valueOf(50000),
                PaymentMethodEnum.MOBILE_PAY.getDisplayName(),
                null,
                "홍길동",
                LocalDateTime.now(),
                PaymentStatusEnum.PAID.getDescription(),
                "https://example.com/paymentId/receipt"
        );

        return ResponseEntity.ok(response);
    }
}
