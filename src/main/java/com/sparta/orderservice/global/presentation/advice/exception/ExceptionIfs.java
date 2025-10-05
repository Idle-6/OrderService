package com.sparta.orderservice.global.presentation.advice.exception;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;

public interface ExceptionIfs {

    ErrorCodeIfs getErrorCode();
    String getDescription();
}
