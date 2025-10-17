package com.sparta.orderservice.review.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCodeIfs {

    InvalidInput(HttpStatus.BAD_REQUEST.value(), 5001, "잘못된 요청 데이터입니다"),
    UnauthorizedAccess(HttpStatus.UNAUTHORIZED.value(), 5002, "인증 정보가 필요합니다"),
    AccessDenied(HttpStatus.FORBIDDEN.value(), 5003, "접근 권한이 없습니다"),
    NotFound(HttpStatus.NOT_FOUND.value(), 5004, "리소스를 찾을 수 없습니다"),
    AlreadyReviewed(HttpStatus.CONFLICT.value(), 5005, "이미 해당 주문에 대한 리뷰가 존재합니다"),
    InvalidSortField(HttpStatus.BAD_REQUEST.value(), 5006, "잘못된 정렬 기준입니다"),
    DataAccessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 5099, "데이터베이스 오류가 발생했습니다"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String errorMessage;
}
