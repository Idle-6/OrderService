package com.sparta.orderservice.order.presentation.advice;


import com.sparta.orderservice.global.presentation.advice.dto.ResExceptionDtoV1;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import com.sparta.orderservice.global.presentation.advice.handler.GlobalExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ResExceptionDtoV1> handleStoreException(ExceptionIfs ex) {
        return GlobalExceptionHandler.handleExceptionCommon(ex);
    }
}