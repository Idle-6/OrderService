package com.sparta.orderservice.manage.presentation.advice.error;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ManageErrorCode implements ErrorCodeIfs {

    //요청 에러
    UnauthorizedAccess(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9001, "인증 정보를 가져오는데 실패했습니다"),
    InvalidInput(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9002, "잘못된 요청 데이터입니다"),
    InvalidSortField(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9003, "잘못된 정렬 기준입니다"),
    //DB 에러
    InvalidData(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9004, "옳지 않은 데이터 형태입니다"),
    DataAccessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9005, "데이터베이스 오류가 발생했습니다"),
    NotFound(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9006, "조회된 결과가 없습니다"),
    //권한 에러
    AccessDenied(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9007, "접근 권한이 없습니다"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String errorMessage;

}
