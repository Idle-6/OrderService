package com.sparta.orderservice.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResOrderDtoV1 {
    private UUID orderId;
    private Integer totalPrice;
    private OrderStatus orderStatus;
    private String storeName;
    private String storeDesc;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public ResOrderDtoV1(UUID orderId, Integer totalPrice, OrderStatus orderStatus, String storeName, String storeDesc, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.storeName = storeName;
        this.storeDesc =  storeDesc;
        this.createdAt = createdAt;
    }
}
