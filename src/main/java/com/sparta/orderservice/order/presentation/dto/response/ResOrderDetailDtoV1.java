package com.sparta.orderservice.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResOrderDetailDtoV1 {
    private UUID orderId;
    private Long userId;
    private UUID storeId;
    private String orderMessage;
    private Integer totalPrice;
    private OrderStatus orderStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @QueryProjection
    public ResOrderDetailDtoV1(UUID orderId, Long userId, UUID storeId, String orderMessage, Integer totalPrice, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.storeId = storeId;
        this.orderMessage = orderMessage;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
