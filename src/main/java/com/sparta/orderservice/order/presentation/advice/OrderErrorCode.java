package com.sparta.orderservice.order.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCodeIfs {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "해당 주문을 찾을 수 없습니다."),
    ORDER_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, 3002, "주문 상태 변경 권한이 없습니다."),
    ORDER_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN, 3003, "주문이 수락된 이후에는 취소할 수 없습니다.");

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