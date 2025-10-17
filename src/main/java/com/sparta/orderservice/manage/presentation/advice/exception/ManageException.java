package com.sparta.orderservice.manage.presentation.advice.exception;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import com.sparta.orderservice.manage.presentation.advice.error.ManageErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ManageException extends RuntimeException implements ExceptionIfs {

    private final ErrorCodeIfs errorCode;
    private final String description;

    public static ManageException notFound(String what) {
        return new ManageException(ManageErrorCode.NotFound, what + " 을(를) 찾을 수 없습니다.");
    }

    public static ManageException invalidInput(String reason) {
        return new ManageException(ManageErrorCode.InvalidInput, reason);
    }

    public static ManageException invalidSortField(String sortBy) {
        return new ManageException(ManageErrorCode.InvalidSortField, "허용되지 않는 정렬 기준: " + sortBy);
    }

    public static ManageException unauthorized(String reason) {
        return new ManageException(ManageErrorCode.UnauthorizedAccess, reason);
    }

    public static ManageException accessDenied(String reason) {
        return new ManageException(ManageErrorCode.AccessDenied, reason);
    }

    public static ManageException dataAccess(Throwable cause) {
        return new ManageException(ManageErrorCode.DataAccessException,
                ManageErrorCode.DataAccessException.getErrorMessage() + " :: " + cause.getMessage());
    }


}
