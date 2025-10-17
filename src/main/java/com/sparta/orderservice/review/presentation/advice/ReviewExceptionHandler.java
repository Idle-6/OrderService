package com.sparta.orderservice.review.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.dto.ResExceptionDtoV1;
import com.sparta.orderservice.global.presentation.advice.handler.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ResExceptionDtoV1> handle(ReviewException ex) {
        return GlobalExceptionHandler.handleExceptionCommon(ex);
    }
}