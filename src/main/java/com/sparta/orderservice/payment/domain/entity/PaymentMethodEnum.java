package com.sparta.orderservice.payment.domain.entity;

public enum PaymentMethodEnum {
    CARD("카드"),                // 신용/체크카드
    CASH("현금"),                // 배달 현장 결제
    MOBILE_PAY("간편결제"),      // 네이버페이, 카카오페이, 토스페이 등
    BANK_TRANSFER("계좌이체"),   // 인터넷/모바일 뱅킹 이체
    VOUCHER("상품권"),           // 배달앱 내 사용 가능한 쿠폰/상품권
    POINT("포인트"),             // 적립금, 마일리지 사용
    QR_PAY("QR결제");            // QR코드 스캔 결제

    private final String displayName;

    PaymentMethodEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
