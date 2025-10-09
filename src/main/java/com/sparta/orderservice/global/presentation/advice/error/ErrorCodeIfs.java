package com.sparta.orderservice.global.presentation.advice.error;

public interface ErrorCodeIfs {

    Integer getHttpStatusCode();
    Integer getErrorCode();
    String getErrorMessage();
}
