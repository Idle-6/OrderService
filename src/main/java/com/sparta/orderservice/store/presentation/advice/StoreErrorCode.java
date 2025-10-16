package com.sparta.orderservice.store.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements ErrorCodeIfs {
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "해당 가게를 찾을 수 없습니다.");

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
