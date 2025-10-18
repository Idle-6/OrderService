package com.sparta.orderservice.order.presentation.dto;

import com.sparta.orderservice.order.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam {

    private Integer totalPrice;
    private OrderStatus orderStatus;
}