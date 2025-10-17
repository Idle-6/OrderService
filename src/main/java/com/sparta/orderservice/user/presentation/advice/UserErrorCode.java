package com.sparta.orderservice.user.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCodeIfs {

    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, 2001, "이미 존재하는 이메일입니다."),
    USER_INVALID_ADMIN_TOKEN(HttpStatus.BAD_REQUEST, 2002, "관리자 암호가 일치하지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 2003, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2004, "회원을 찾을 수 없습니다."),
    USER_INACTIVE(HttpStatus.BAD_REQUEST, 2005, "이미 탈퇴한 사용자입니다."),
    USER_UNKNOWN_AUTHORITY(HttpStatus.BAD_REQUEST, 2006, "알 수 없는 권한입니다.");

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
