package com.sparta.orderservice.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResOrderDetailDtoV1 {
    private UUID orderId;
    private String orderMessage;
    private Integer totalPrice;
    private OrderStatus orderStatus;

    private String storeName;
    private String storeDesc;
    private List<ReqOrderMenuDtoV1> orderMenus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @QueryProjection
    public ResOrderDetailDtoV1(UUID orderId, String orderMessage, Integer totalPrice, OrderStatus orderStatus, String storeName, String storeDesc, List<ReqOrderMenuDtoV1> orderMenus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.orderMessage = orderMessage;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.storeName = storeName;
        this.storeDesc = storeDesc;
        this.orderMenus = (orderMenus != null) ? orderMenus : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
