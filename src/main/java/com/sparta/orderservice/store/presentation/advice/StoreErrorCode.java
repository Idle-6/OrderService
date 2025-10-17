package com.sparta.orderservice.store.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements ErrorCodeIfs {
    STORE_ALREADY_OWNED(HttpStatus.BAD_REQUEST, 6001, "이미 가게를 소유한 사용자입니다."),
    STORE_FORBIDDEN(HttpStatus.FORBIDDEN, 6002, "가게 수정/삭제 권한이 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 6003, "해당 가게를 찾을 수 없습니다.")
    ;

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
