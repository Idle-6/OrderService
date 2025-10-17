package com.sparta.orderservice.payment.presentation.controller;

import com.sparta.orderservice.payment.application.PaymentServiceV1;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentControllerV1 {

    private final PaymentServiceV1 paymentService;

    @PostMapping
    public ResponseEntity<ResPaymentDtoV1> completePayment(@RequestBody @Valid ReqPaymentDtoV1 request) {
        ResPaymentDtoV1 response = paymentService.completePayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResPaymentSummaryDtoV1>> getPaymentList(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        Page<ResPaymentSummaryDtoV1> response = paymentService.getPaymentPage(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResPaymentDtoV1> getPayment(@PathVariable(name = "paymentId") UUID paymentId) {
        ResPaymentDtoV1 response = paymentService.getPayment(paymentId);

        return ResponseEntity.ok(response);
    }
}
