package com.sparta.orderservice.payment.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatusEnum {
    PAID("결제 완료"),           // 결제 성공, 완료 상태
    CANCELED("결제 취소"),       // 결제 취소됨
    REFUNDED("결제 환불")        // 환불 완료됨
    ;

    private final String description;

    PaymentStatusEnum(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PaymentStatusEnum fromDescription(String description) {
        for(PaymentStatusEnum status : PaymentStatusEnum.values()) {
            if(status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid display name");
    }
}