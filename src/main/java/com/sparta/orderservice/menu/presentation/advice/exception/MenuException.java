package com.sparta.orderservice.menu.presentation.advice.exception;

import com.sparta.orderservice.global.presentation.advice.error.ErrorCodeIfs;
import com.sparta.orderservice.global.presentation.advice.exception.ExceptionIfs;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.menu.presentation.advice.error.MenuErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MenuException extends RuntimeException implements ExceptionIfs {

    private final ErrorCodeIfs errorCode;
    private final String description;

    public static MenuException InvalidMenuDataOnCreateMenu(Exception e, MenuEntity menuEntity) {
        return new MenuException(MenuErrorCode.InvalidMenuData,
                "메뉴 생성 : 잘못된 menuEntity 요청\n" +
                "menuEntity : " + menuEntity.toString() + "\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException DataAccessExceptionOnCreateMenu(Exception e) {
        return new MenuException(MenuErrorCode.DataAccessException,
                "메뉴 생성 : 데이터베이스 에러\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException UnauthorizedAccessOnGetMenuList() {
        return new MenuException(MenuErrorCode.UnauthorizedAccess,
                "메뉴 리스트 조회 : userDetails가 null입니다\n");
    }

    public static MenuException AccessDeniedOnGetMenuList() {
        return new MenuException(MenuErrorCode.AccessDenied,
                "메뉴 리스트 조회 : 접근 권한이 없습니다\n");
    }

    public static MenuException InvalidSortFieldOnGetMenuList(Exception e) {
        return new MenuException(MenuErrorCode.InvalidSortField,
                "메뉴 리스트 조회 : 잘못된 sortBy 값\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException DataAccessExceptionOnGetMenuList(DataAccessException e) {
        return new MenuException(MenuErrorCode.DataAccessException,
                "메뉴 리스트 조회 : 데이터베이스 오류\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException MenuNotFoundOnGetMenu(UUID menuId) {
        return new MenuException(MenuErrorCode.MenuNotFound,
                "메뉴 상세 조회 : id가 일치하는 메뉴가 없습니다\n" +
                "menu id : " + menuId.toString() + "\n");
    }

    public static MenuException DataAccessExceptiondOnGetMenu(DataAccessException e) {
        return new MenuException(MenuErrorCode.DataAccessException,
                "메뉴 상세 조회 : 데이터베이스 오류\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException MenuNotFoundOnUpdateMenu(UUID menuId) {
        return new MenuException(MenuErrorCode.MenuNotFound,
                "메뉴 수정 : id가 일치하는 메뉴가 없습니다\n" +
                "menu id : " + menuId.toString() + "\n");
    }

    public static MenuException DataAccessExceptionOnUpdateMenu(DataAccessException e) {
        return new MenuException(MenuErrorCode.DataAccessException,
                "메뉴 수정 : 데이터베이스 오류\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException MenuNotFoundOnDeleteMenu(UUID menuId) {
        return new MenuException(MenuErrorCode.MenuNotFound,
                "메뉴 삭제 : id가 일치하는 메뉴가 없습니다\n" +
                "menu id : " + menuId.toString() + "\n");
    }

    public static MenuException DataAccessExceptionOnDeleteMenu(DataAccessException e) {
        return new MenuException(MenuErrorCode.DataAccessException,
                "메뉴 삭제 : 데이터베이스 오류\n" +
                "에러 메세지 : " + e.getMessage());
    }

    public static MenuException AiApiClientException(HttpClientErrorException e) {
        return new MenuException(MenuErrorCode.AiApiCallFail,
                "AI API 오류 : 클라이언트 요청 오류\n" +
                "status : " + e.getStatusCode() + "\n" +
                "response : " + e.getResponseBodyAsString()
        );
    }

    public static MenuException AiApiServerException(HttpServerErrorException e) {
        return new MenuException(MenuErrorCode.AiApiCallFail,
                "AI API 오류 : 서버 오류\n" +
                "status : " + e.getStatusCode() + "\n" +
                "response : " + e.getResponseBodyAsString()
        );
    }

    public static MenuException AiApiNetworkException(ResourceAccessException e) {
        return new MenuException(MenuErrorCode.AiApiCallFail,
                "AI API 오류 : 네트워크 접근 오류\n" +
                "에러 메세지 : " + e.getMessage()
        );
    }

    public static MenuException AiApiRestClientException(RestClientException e) {
        return new MenuException(MenuErrorCode.AiApiCallFail,
                "AI API 오류 : Rest 클라이언트 예외 발생\n" +
                "에러 메세지 : " + e.getMessage()
        );
    }

    public static MenuException AiApiUnknownException(Exception e) {
        return new MenuException(MenuErrorCode.AiApiCallFail,
                "AI API 오류 : AI 서비스 호출 중 알 수 없는 오류가 발생했습니다\n"
        );
    }
}
