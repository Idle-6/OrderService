package com.sparta.orderservice.payment.domain.repository;

import com.sparta.orderservice.payment.presentation.dto.SearchParam;
import com.sparta.orderservice.payment.presentation.dto.response.ResManagerPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResStorePaymentDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomPaymentRepository {

    Page<ResManagerPaymentDtoV1> findPaymentPage(SearchParam searchParam, Pageable pageable);

    Page<ResStorePaymentDtoV1> findPaymentPageByStoreId(UUID storeId, SearchParam searchParam, Pageable pageable);

    Page<ResPaymentSummaryDtoV1> findPaymentPageByUserId(Long userId, SearchParam searchParam, Pageable pageable);

    Optional<ResPaymentDtoV1> findPaymentById(UUID paymentId);
  
    Optional<ResPaymentDtoV1> findPaymentByOrderId(UUID orderId, Long userId);
}
