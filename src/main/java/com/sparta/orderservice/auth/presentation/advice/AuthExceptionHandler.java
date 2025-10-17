package com.sparta.orderservice.auth.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.dto.ResExceptionDtoV1;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import com.sparta.orderservice.global.presentation.advice.handler.GlobalExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResExceptionDtoV1> handleUserException(ExceptionIfs ex){
        return GlobalExceptionHandler.handleExceptionCommon(ex);
    }
}
