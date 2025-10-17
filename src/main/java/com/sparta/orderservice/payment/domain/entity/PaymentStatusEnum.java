package com.sparta.orderservice.payment.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatusEnum {
    PAID("결제 완료"),           // 결제 성공, 완료 상태
    FAILED("결제 실패"),         // 결제 실패
    CANCELED("결제 취소"),       // 결제 취소됨
    REFUNDED("결제 환불"),       // 환불 완료됨
    ERROR("결제 오류");          // 결제 처리 중 오류 발생

    private final String description;

    PaymentStatusEnum(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}