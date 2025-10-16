package com.sparta.orderservice.manage.presentation.controller;

import com.sparta.orderservice.manage.application.service.ManageServiceV1;
import com.sparta.orderservice.manage.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/manages")
public class ManageControllerV1 {

     private final ManageServiceV1 manageService;

    /* 회원 조회 */
    @GetMapping("/users")
    public ResponseEntity<List<ResUserDtoV1>> getUserList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc
    ){
        List<ResUserDtoV1> userDtoList = manageService.getUserList(search, page, pageSize, sortBy, isAsc);
        return ResponseEntity.ok(userDtoList);
    }

    /* 회원 상세 조회 */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResUserDetailDtoV1> getUser(
            @PathVariable Long userId
    ){
        ResUserDetailDtoV1 userDetailDto = manageService.getUser(userId);
        return ResponseEntity.ok(userDetailDto);
    }


    /* 회원 비활성화 */
    @PostMapping("/users/{userId}/deactive")
    public ResponseEntity<?> userDeactive(
            @PathVariable Long userId
    ){
        manageService.userDeactive(userId);
        return ResponseEntity.ok("회원 비활성화 완료");
    }

    /* 회원 활성화 */
    @PostMapping("/users/{userId}/active")
    public ResponseEntity<?> userActive(
            @PathVariable Long userId
    ){
        manageService.userActive(userId);
        return ResponseEntity.ok("회원 활성화 완료");
    }

    /* 가게 조회 */
    @GetMapping("/store-list")
    public ResponseEntity<List<ResStoreDtoV1>> getStoreList(
            @RequestParam(required = false, defaultValue = "1") String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){

        List<ResStoreDtoV1> storeDtoList = manageService.getStoreList(search, page, pageSize, sortBy, isAsc);
        return ResponseEntity.ok(storeDtoList);
    }

    /* 가게 상세 조회 */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> getStore(
            @PathVariable UUID storeId
    ){
        ResStoreDetailDtoV1 storeDetail = manageService.getStore(storeId);
        return ResponseEntity.ok(storeDetail);
    }

    /* 가게 비활성화 */
    @PostMapping("/stores/{storeId}/deactive")
    public ResponseEntity<?> storeDeactive(
            @PathVariable UUID storeId
    ){
        manageService.storeDeactive(storeId);
        return ResponseEntity.ok("가게 비활성화 완료");
    }

    /* 가게 활성화 */
    @PostMapping("/stores/{storeId}/active")
    public ResponseEntity<?> storeActive(
            @PathVariable UUID storeId
    ){
        manageService.storeActive(storeId);
        return ResponseEntity.ok("가게 활성화 완료");
    }

    /* 주문 리스트 조회 */
    @GetMapping("/orders")
    public ResponseEntity<List<ResOrderDtoV1>> getOrderList(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc
    ){
        List<ResOrderDtoV1> orderDtoList = manageService.getOrderList(search, page, pageSize, sortBy, isAsc);
        return ResponseEntity.ok(orderDtoList);
    }

    /* 주문 상세 조회 */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResOrderDetailDtoV1> getOrder(
            @PathVariable Integer orderId
    ){
        ResOrderDetailDtoV1 orderDetail = manageService.getOrder(orderId);
        return ResponseEntity.ok(orderDetail);
    }
}

