package com.sparta.orderservice.global.presentation.advice.error;

public interface ErrorCodeIfs {

    public Integer getHttpStatusCode();
    public Integer getErrorCode();
    public String getErrorMessage();
}
