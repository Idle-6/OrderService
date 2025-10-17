package com.sparta.orderservice.menu.presentation.advice.error;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MenuErrorCode implements ErrorCodeIfs {
    //요청 에러
    UnauthorizedAccess(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8001, "인증 정보를 가져오는데 실패했습니다"),
//    InvalidInput(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8002, "잘못된 요청 데이터입니다"),
    InvalidSortField(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8003, "잘못된 정렬 기준입니다"),
    //DB 에러
    InvalidMenuData(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8004, "옳지 않은 메뉴 데이터 형태입니다"),
    DataAccessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8005, "메뉴 데이터베이스 오류가 발생했습니다"),
    MenuNotFound(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8006, "조회된 메뉴가 없습니다"),
    //기타 메뉴 서비스 에러
    AccessDenied(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8007, "접근 권한이 없습니다"),
    //AI 에러
    AiApiCallFail(HttpStatus.INTERNAL_SERVER_ERROR.value(), 8500, "AI API 호출에 실패하였습니다"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String errorMessage;
}
