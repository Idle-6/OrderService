package com.sparta.orderservice.auth.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCodeIfs {

    AUTH_NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 1001, "토큰이 존재하지 않습니다."),
    AUTH_TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, 1001, "요청된 토큰 타입과 일치하지 않습니다."),
    AUTH_INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, 1001, "유효하지 않은 JWT 서명입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 1001, "만료된 JWT 토큰입니다."),
    AUTH_UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, 1001, "지원되지 않는 JWT 토큰입니다."),
    AUTH_INVALID_CLAIMS(HttpStatus.BAD_REQUEST, 1001, "잘못된 JWT 토큰입니다.");

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
