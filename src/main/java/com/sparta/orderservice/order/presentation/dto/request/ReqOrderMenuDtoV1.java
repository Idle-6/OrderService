package com.sparta.orderservice.order.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqOrderMenuDtoV1 {
    private UUID menuId;
    private int orderMenuQty;
}
