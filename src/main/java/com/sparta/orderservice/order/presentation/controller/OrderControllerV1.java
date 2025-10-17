package com.sparta.orderservice.order.presentation.controller;

import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.order.application.service.OrderServiceV1;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.presentation.dto.request.*;
import com.sparta.orderservice.order.presentation.dto.response.*;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderControllerV1 {
    private final OrderServiceV1 orderService;

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ResOrderDetailDtoV1> getOrder(@PathVariable UUID orderId) {
        ResOrderDetailDtoV1 response = orderService.getOrderDetail(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 리스트 조회
    @GetMapping
    public ResponseEntity<Page<ResOrderDtoV1>> getOrderList(@RequestParam(required = false) OrderStatus orderStatus,
                                                            @RequestParam(required = false) Integer totalPrice,
                                                            @PageableDefault(size = 10) Pageable pageable) {

        SearchParam searchParam = new SearchParam(totalPrice, orderStatus);
        Page<ResOrderDtoV1> response = orderService.getOrders(searchParam, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<ResOrderDtoV1> createOrder(@RequestBody ReqOrderDtoV1 request,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResOrderDtoV1 response = orderService.createOrder(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 상태 변경
    @PatchMapping("/{orderId}/{orderStatus}")
    public ResponseEntity<ResOrderUpdateDtoV1> updateOrderStatus(
            @PathVariable UUID orderId,
            @PathVariable OrderStatus orderStatus,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ResOrderUpdateDtoV1 response = orderService.updateOrderStatus(orderId, orderStatus, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ResOrderCancelDtoV1> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ResOrderCancelDtoV1 response = orderService.cancelOrder(orderId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }


}
