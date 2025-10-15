package com.sparta.orderservice.user.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class UserException extends RuntimeException implements ExceptionIfs {

    private final UserErrorCode errorCode;
    private final String description;

    public UserException(UserErrorCode errorCode) {
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
