package com.sparta.orderservice.payment.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCodeIfs {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "결제 내역을 찾을 수 없습니다."),
    PAYMENT_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN, 1001, "결제 취소 권한이 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final Integer errorCode;
    private final String message;

    @Override
    public Integer getHttpStatusCode() {
        return httpStatus.value();
    }

    @Override
    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }
}
