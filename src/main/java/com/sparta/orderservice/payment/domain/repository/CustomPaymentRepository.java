package com.sparta.orderservice.payment.domain.repository;

import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomPaymentRepository {

    Page<ResPaymentSummaryDtoV1> findPaymentPageByUserId(Long userId, Pageable pageable);

    Optional<ResPaymentDtoV1> findPaymentById(UUID paymentId);
}
