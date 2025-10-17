package com.sparta.orderservice.order.presentation.advice;

import java.util.UUID;

public class OrderExceptionLogUtils {
    public static String getNotFoundMessage(UUID orderId, Long userId) {
        return "주문 없음: orderId=%s, 요청 사용자 ID=%s".formatted(orderId, userId);
    }

    public static String getUpdateForbiddenMessage(UUID orderId, Long userId) {
        return "가게 수정 권한 없음: orderId=%s, 사용자 ID=%s".formatted(orderId, userId);
    }

    public static String getCancelForbiddenMessage(UUID orderId, Long userId) {
        return "주문 수락 후에는 취소할 수 없음: orderId=%s, 사용자 ID=%s".formatted(orderId, userId);
    }
}