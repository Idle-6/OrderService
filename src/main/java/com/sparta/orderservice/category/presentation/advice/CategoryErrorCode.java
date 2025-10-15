package com.sparta.orderservice.category.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCodeIfs {

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "카테고리를 찾을 수 없습니다."),
    CATEGORY_CONFLICT(HttpStatus.CONFLICT, 1001, "이미 존재하는 카테고리 입니다.");

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
