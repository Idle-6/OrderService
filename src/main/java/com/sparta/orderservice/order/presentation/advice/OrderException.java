package com.sparta.orderservice.order.presentation.advice;


import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class OrderException extends RuntimeException implements ExceptionIfs {

    private final OrderErrorCode errorCode;
    private final String description;

    public OrderException(OrderErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.description = errorCode.getErrorMessage();
    }

    public OrderException(OrderErrorCode errorCode, String description) {
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