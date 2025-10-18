package com.sparta.orderservice.payment.presentation.advice;

import java.util.UUID;

public class PaymentExceptionLogUtils {
    public static String getNotFoundMessage(UUID paymentId, Long userId) {
        return "결제 내역 없음: paymentId=%s, 요청 사용자 ID=%s".formatted(paymentId, userId);
    }

    public static String getUpdateStatusMessage(UUID paymentId, Long userId) {
        return "결제 상태 수정 권한 없음: paymentId=%s, 사용자 ID=%s".formatted(paymentId, userId);
    }

    public static String getCancelMessage(UUID paymentId, Long userId) {
        return "결제 취소 권한 없음: paymentId=%s, 사용자 ID=%s".formatted(paymentId, userId);
    }

    public static String getViewForbiddenMessage(UUID storeId, Long userId) {
        return "결제 내역 조회 권한 없음: storeId=%s, 사용자 ID=%s".formatted(storeId, userId);
    }
}
