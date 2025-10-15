package com.sparta.orderservice.payment.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class PaymentException extends RuntimeException implements ExceptionIfs {

    private final PaymentErrorCode errorCode;
    private final String description;

    public PaymentException(PaymentErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
    }

    @Override
    public ErrorCodeIfs getErrorCode() {
        return errorCode;
    }

    @Override
    public String getDescription() {
        return description == null ? errorCode.getErrorMessage() : description;
    }
}
