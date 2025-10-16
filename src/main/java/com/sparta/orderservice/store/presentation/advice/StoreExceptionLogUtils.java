package com.sparta.orderservice.store.presentation.advice;

import java.util.UUID;

public class StoreExceptionLogUtils {
    public static String getNotFoundMessage(UUID storeId) {
        return "가게 없음: storeId=%s".formatted(storeId);
    }

    public static String getNotFoundMessage(UUID storeId, Long userId) {
        return "가게 없음: storeId=%s, 요청 사용자 ID=%s".formatted(storeId, userId);
    }

    public static String getAlreadyOwnedMessage(Long userId) {
        return "이미 가게를 소유한 사용자입니다: 사용자 ID=%s".formatted(userId);
    }

    public static String getUpdateForbiddenMessage(UUID storeId, Long userId) {
        return "가게 수정 권한 없음: storeId=%s, 사용자 ID=%s".formatted(storeId, userId);
    }

    public static String getDeleteForbiddenMessage(UUID storeId, Long userId) {
        return "가게 삭제 권한 없음: storeId=%s, 사용자 ID=%s".formatted(storeId, userId);
    }
}
