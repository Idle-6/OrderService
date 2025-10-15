package com.sparta.orderservice.order.presentation.dto.request;

import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqOrderDtoV1 {
    private int totalPrice;
    private String orderMessage;
    private OrderStatus orderStatus;
    private Store storeId;
    private User userId;
}
