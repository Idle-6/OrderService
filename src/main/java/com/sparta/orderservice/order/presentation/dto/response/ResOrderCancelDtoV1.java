package com.sparta.orderservice.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderCancelDtoV1 {
    private UUID orderId;
    private OrderStatus orderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 결제 취소 정보 응답 추가 예정
}