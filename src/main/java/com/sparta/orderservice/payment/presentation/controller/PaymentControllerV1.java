package com.sparta.orderservice.payment.presentation.controller;

import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.payment.application.PaymentServiceV1;
import com.sparta.orderservice.payment.presentation.dto.SearchParam;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentControllerV1 {

    private final PaymentServiceV1 paymentService;

    @PostMapping
    public ResponseEntity<ResPaymentDtoV1> completePayment(@RequestBody @Valid ReqPaymentDtoV1 request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResPaymentDtoV1 response = paymentService.completePayment(request, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<?>> getPaymentList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Page<?> response;
        SearchParam searchParam = new SearchParam(search);

        if (storeId != null) {
            response = paymentService.getPaymentPageForOwner(userDetails.getUser() ,storeId, searchParam, pageable);
        } else if (userDetails.getUser().getRole() == UserRoleEnum.ADMIN) {
            response = paymentService.getPaymentPageForManager(searchParam, pageable);
        } else {
            Long userId = userDetails.getUser().getUserId();
            response = paymentService.getPaymentPage(pageable, searchParam, userId);
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{paymentId}")
    public ResponseEntity<ResPaymentDtoV1> getPayment(@PathVariable(name = "paymentId") UUID paymentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResPaymentDtoV1 response = paymentService.getPayment(paymentId, userDetails.getUser().getUserId());

        return ResponseEntity.ok(response);
    }
}
