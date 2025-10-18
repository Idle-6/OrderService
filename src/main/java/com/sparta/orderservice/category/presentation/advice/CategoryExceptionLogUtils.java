package com.sparta.orderservice.category.presentation.advice;

import java.util.UUID;

public class CategoryExceptionLogUtils {

    public static String getNotFoundMessage(UUID categoryId) {
        return "카테고리 없음: categoryId=%s".formatted(categoryId);
    }

    public static String getNotFoundMessage(UUID categoryId, Long userId) {
        return "카테고리 없음: categoryId=%s, 요청 사용자 ID=%s".formatted(categoryId, userId);
    }

    public static String getConflictMessage(String name, Long userId) {
        return "카테고리 중복 발생: 이름='%s', 요청 사용자 ID=%s".formatted(name, userId);
    }

    public static String getDeleteMessage(UUID categoryId, Long userId) {
        return "카테고리 삭제 불가: categoryId=%s, 요청 사용자 ID=%s (가게가 존재함)".formatted(categoryId, userId);
    }
}
