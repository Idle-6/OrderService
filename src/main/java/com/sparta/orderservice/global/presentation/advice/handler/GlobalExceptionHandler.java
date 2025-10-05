package com.sparta.orderservice.global.presentation.advice.handler;

import com.sparta.orderservice.global.presentation.advice.dto.ResExceptionDtoV1;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResExceptionDtoV1> handleException(Exception ex){
        log.error(ex.getMessage());

        ResExceptionDtoV1 resExceptionDtoV1 = ResExceptionDtoV1.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resExceptionDtoV1);
    }

    public static ResponseEntity<ResExceptionDtoV1> handleExceptionCommon(ExceptionIfs ex){

        log.error(ex.getDescription());

        ResExceptionDtoV1 resExceptionDtoV1 = ResExceptionDtoV1.builder()
                .errorCode(ex.getErrorCode().getErrorCode())
                .message(ex.getErrorCode().getErrorMessage())
                .build();

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatusCode())
                .body(resExceptionDtoV1);
    }


//    //========== 도메인별 예외처리 ==========
//    //핸들러 클래스 따로 생성해서 그곳에 작성
//    //사전작업1. XXXException 클래스 구현        :   public class XXXException implements ExceptionIfs
//    //사전작업2. XXXErrorCode enum 클래스 구현   :   public class enum XXXErrorCode implements ErrorCodeIfs
//    @ExceptionHandler(XXXException.class)
//    public ResponseEntity<ResExceptionDtoV1> handleXXXException(ExceptionIfs ex){
//
//        //TODO 예외처리 작업
//
//        return GlobalExceptionHandler.handleExceptionCommon(ex);
//    }
}
