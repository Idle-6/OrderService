package com.sparta.orderservice.manage.presentation.advice.handler;

import com.sparta.orderservice.global.presentation.advice.dto.ResExceptionDtoV1;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import com.sparta.orderservice.global.presentation.advice.handler.GlobalExceptionHandler;
import com.sparta.orderservice.manage.presentation.advice.exception.ManageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ManageExceptionHandler {

    @ExceptionHandler(ManageException.class)
    public ResponseEntity<ResExceptionDtoV1>  handleMenuException(ExceptionIfs ex) {
        return GlobalExceptionHandler.handleExceptionCommon(ex);
    }

}
