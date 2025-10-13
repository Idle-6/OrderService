package com.sparta.orderservice.category.presentation.advice;

import java.util.UUID;

public class CategoryExceptionLogUtils {
    public static String getNotFoundMessage(UUID categoryId, Long userId) {
        return "카테고리 없음: categoryId=%s, 요청 사용자 ID=%s".formatted(categoryId, userId);
    }

    public static String getConflictMessage(String name, Long userId) {
        return "카테고리 중복 발생: 이름='%s', 요청 사용자 ID=%s".formatted(name, userId);
    }
}
