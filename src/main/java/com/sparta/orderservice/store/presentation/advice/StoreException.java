package com.sparta.orderservice.store.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class StoreException extends RuntimeException implements ExceptionIfs {

    private final StoreErrorCode errorCode;
    private final String description;

    public StoreException(StoreErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.description = errorCode.getErrorMessage();
    }

    public StoreException(StoreErrorCode errorCode, String description) {
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
