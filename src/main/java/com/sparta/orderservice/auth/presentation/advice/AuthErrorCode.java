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
    AUTH_TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, 1002, "요청된 토큰 타입과 일치하지 않습니다."),
    AUTH_INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, 1003, "유효하지 않은 JWT 서명입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 1004, "만료된 JWT 토큰입니다."),
    AUTH_UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, 1005, "지원되지 않는 JWT 토큰입니다."),
    AUTH_INVALID_CLAIMS(HttpStatus.BAD_REQUEST, 1006, "토큰 정보가 올바르지 않습니다."),
    AUTH_INVALID_USER_DETAILS(HttpStatus.BAD_REQUEST, 1007, "인증 정보가 올바르지 않습니다."),
    AUTH_NO_VERIFICATION(HttpStatus.NOT_FOUND, 1008, "인증 요청이 존재하지 않습니다."),
    AUTH_EXPIRED_VERIFICATION(HttpStatus.UNAUTHORIZED, 1009, "인증번호가 만료되었습니다."),
    AUTH_USED_VERIFICATION(HttpStatus.IM_USED, 1010, "이미 사용된 인증번호 입니다."),
    AUTH_VERIFICATION_MISMATCH(HttpStatus.UNAUTHORIZED, 1011, "인증번호가 일치하지 않습니다."),
    AUTH_EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1012, "이메일 전송에 실패했습니다."),
    AUTH_EMAIL_ENCODING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1013, "이메일 인코딩에 실패했습니다."),
    AUTH_HASH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1014, "인증번호 해싱에 실패했습니다.");

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
