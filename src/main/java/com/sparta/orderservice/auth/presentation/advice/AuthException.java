package com.sparta.orderservice.auth.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class AuthException extends RuntimeException implements ExceptionIfs {

    private final AuthErrorCode errorCode;
    private final String description;

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.description = errorCode.getErrorMessage();
    }

    @Override
    public ErrorCodeIfs getErrorCode() {
        return errorCode;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
