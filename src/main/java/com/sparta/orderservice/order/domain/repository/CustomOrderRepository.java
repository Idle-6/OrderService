package com.sparta.orderservice.order.domain.repository;

import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomOrderRepository {

    // 검색 조건에 따른 주문 목록 조회
    Page<ResOrderDtoV1> findOrderPage(SearchParam searchParam, Pageable pageable, User user);

    // 주문 번호 기준으로 주문 상세 정보 조회
    Optional<ResOrderDetailDtoV1> findOrderDetailById(UUID orderId);

    // 유저 ID 기준으로 해당 유저가 생성한 주문 상세 정보 조회
    Optional<ResOrderDetailDtoV1> findOrderDetailByUserId(Long userId);

}
