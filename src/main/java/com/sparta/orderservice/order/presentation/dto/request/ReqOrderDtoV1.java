package com.sparta.orderservice.order.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqOrderDtoV1 {
    private int totalPrice;
    private String orderMessage;
    private String storeId;
    private Long userId;
}
