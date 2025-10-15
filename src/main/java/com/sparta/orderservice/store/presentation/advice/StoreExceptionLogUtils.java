package com.sparta.orderservice.store.presentation.advice;

import java.util.UUID;

public class StoreExceptionLogUtils {
    public static String getNotFoundMessage(UUID storeId, Long userId) {
        return "가게 없음: storeId=%s, 요청 사용자 ID=%s".formatted(storeId, userId);
    }
}
