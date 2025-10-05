package com.sparta.orderservice.global.presentation.advice.exception;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;

public interface ExceptionIfs {

    public ErrorCodeIfs getErrorCode();
    public String getDescription();
}
