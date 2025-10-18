package com.sparta.orderservice.category.presentation.advice;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;

public class CategoryException extends RuntimeException implements ExceptionIfs {

    private final CategoryErrorCode errorCode;
    private final String description;

    public CategoryException(CategoryErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
    }

    @Override
    public ErrorCodeIfs getErrorCode() {
        return errorCode;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

