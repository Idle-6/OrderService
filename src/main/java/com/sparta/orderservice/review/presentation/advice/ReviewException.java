package com.sparta.orderservice.review.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewException extends RuntimeException implements ExceptionIfs {

    private final ErrorCodeIfs errorCode;
    private final String description;

    public static ReviewException invalidInput(String d)      { return new ReviewException(ReviewErrorCode.InvalidInput, d); }
    public static ReviewException unauthorized(String d)      { return new ReviewException(ReviewErrorCode.UnauthorizedAccess, d); }
    public static ReviewException accessDenied(String d)      { return new ReviewException(ReviewErrorCode.AccessDenied, d); }
    public static ReviewException notFound(String d)          { return new ReviewException(ReviewErrorCode.NotFound, d); }
    public static ReviewException alreadyReviewed(String d)   { return new ReviewException(ReviewErrorCode.AlreadyReviewed, d); }
    public static ReviewException invalidSortField(String d)  { return new ReviewException(ReviewErrorCode.InvalidSortField, d); }
    public static ReviewException dataAccess(Exception e)     { return new ReviewException(ReviewErrorCode.DataAccessException, e.getMessage()); }
}