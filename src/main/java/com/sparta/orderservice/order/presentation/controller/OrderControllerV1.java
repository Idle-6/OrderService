package com.sparta.orderservice.order.presentation.controller;

import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.presentation.dto.request.*;
import com.sparta.orderservice.order.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderControllerV1 {
    // 주문 상세 조회

    @GetMapping("/{orderId}")
    public ResponseEntity<ResOrderDetailDtoV1> getOrder(@PathVariable UUID orderId) {
        ResOrderDetailDtoV1 orderDetailDto = new ResOrderDetailDtoV1(
                orderId,
                1001L,
                UUID.randomUUID(),
                "문 앞에 두고  가주세요",
                25000,
                OrderStatus.START,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(orderDetailDto, HttpStatus.OK);
    }

    // 주문 리스트 조회
    @GetMapping
    public ResponseEntity<List<ResOrderDtoV1>> getOrderList(@RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(required = false, defaultValue = "id") String sortBy,
                                         @RequestParam(required = false, defaultValue = "true") boolean isAsc) {

        List<ResOrderDtoV1> orderList = Arrays.asList(
                new ResOrderDtoV1(UUID.randomUUID(), 1001L, UUID.randomUUID(), 20000, OrderStatus.START, LocalDateTime.now().minusDays(2)),
                new ResOrderDtoV1(UUID.randomUUID(), 1002L, UUID.randomUUID(), 35000, OrderStatus.COMPLETE, LocalDateTime.now().minusDays(1))
        );
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<ResOrderDtoV1> createOrder(@RequestBody ReqOrderDtoV1 request) {
        ResOrderDtoV1 createdOrder = new ResOrderDtoV1(
                UUID.randomUUID(),
                request.getUserId().getUserId(),
                UUID.randomUUID(),
                request.getTotalPrice(),
                OrderStatus.CREATED,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(createdOrder, HttpStatus.OK);
    }

    // 주문 상태 변경
    @PatchMapping("/{orderId}/{orderStatus}")
    public ResponseEntity<ResOrderUpdateDtoV1> updateOrderStatus(
            @PathVariable UUID orderId,
            @PathVariable String orderStatus
    ) {
        ResOrderUpdateDtoV1 updatedOrder = new ResOrderUpdateDtoV1(
                orderId,
                OrderStatus.ACCEPTED,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ResOrderCancelDtoV1> cancelOrder(
            @PathVariable UUID orderId
    ) {
        ResOrderCancelDtoV1 canceledOrder = new ResOrderCancelDtoV1(
                UUID.randomUUID(),
                OrderStatus.CREATED,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(canceledOrder, HttpStatus.OK);
    }


}
