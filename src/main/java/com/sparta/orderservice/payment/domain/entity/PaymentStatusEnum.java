package com.sparta.orderservice.payment.domain.entity;

public enum PaymentStatusEnum {
    PENDING("결제 대기 중"),      // 결제 요청 및 승인 대기 상태
    PAID("결제 완료"),           // 결제 성공, 완료 상태
    FAILED("결제 실패"),         // 결제 실패
    CANCELED("결제 취소"),       // 결제 취소됨
    REFUNDED("결제 환불"),       // 환불 완료됨
    ERROR("결제 오류");          // 결제 처리 중 오류 발생

    private final String description;

    PaymentStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}