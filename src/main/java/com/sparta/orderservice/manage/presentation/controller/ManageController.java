package com.sparta.orderservice.manage.presentation.controller;

import com.sparta.orderservice.manage.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manages")
public class ManageController {

    // private final ManageServise manageService;

    /* 회원 조회 */
    @GetMapping("/customer-list/{search}")
    public ResponseEntity<List<ResCustomerDto>> getCustomerList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc
    ){
//        List<ResCustomerDto> customerDtoList = manageService.getCustomerList();
//        return ResponseEntity.ok(customerDtoList);
        List<ResCustomerDto> customerDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 회원 상세 조회 */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<ResCustomerDto>> getCustomer(
            @PathVariable Integer customerId
    ){
//        ResCustomerDetailDto customerDetailDto = manageService.getCustomerDetail();
//        return ResponseEntity.ok(customerDetailDto);
        ResCustomerDetailDto customerDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /* 회원 비활성화 */
    @PostMapping("/customers/{customerId}/deactive")
    public ResponseEntity<?> customerDeactive(
            @PathVariable Integer customerId
    ){
//        manageService.customerDeactive();
//        return ResponseEntity.ok("회원 비활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 회원 활성화 */
    @PostMapping("/customers/{customerId}/active")
    public ResponseEntity<?> customerActive(
            @PathVariable Integer customerId
    ){
//        manageService.customerActive();
//        return ResponseEntity.ok("회원 활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 조회 */
    @GetMapping("/store-list/{search}")
    public ResponseEntity<List<ResStoreDto>> getStoreList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){
//        List<ResStoreDto> storeDtoList = manageService.getStoreList();
//        return ResponseEntity.ok(storeDtoList);
        List<ResStoreDto> storeDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 상세 조회 */
    @GetMapping("/stores/{ownerId}")
    public ResponseEntity<ResStoreDetailDto> getStore(
            @PathVariable Integer ownerId
    ){
//        ResStoreDetailDto storeDetailDto = manageService.getStore();
//        return ResponseEntity.ok(storeDetailDto);
        ResStoreDetailDto storeDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 비활성화 */
    @PostMapping("/stores/{ownerId}/deactive")
    public ResponseEntity<?> storeDeactive(
            @PathVariable Integer ownerId
    ){
//        manageService.storeDeactive();
//        return ResponseEntity.ok("가게 비활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 활성화 */
    @PostMapping("/stores/{ownerId}/active")
    public ResponseEntity<?> storeActive(
            @PathVariable Integer ownerId
    ){
//        manageService.storeActive();
//        return ResponseEntity.ok("가게 활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 주문 리스트 조회 */
    @GetMapping("/order-list/{search}")
    public ResponseEntity<List<ResOrderDto>> getOrderList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){
//        List<ResOrderDto> orderDtoList = manageService.getOrderList();
//        return ResponseEntity.ok(orderDtoList);
        List<ResOrderDto> orderDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 주문 상세 조회 */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResOrderDetailDto> getOrder(
            @PathVariable Integer orderId
    ){
//        ResOrderDetailDto orderDetailDto = manageService.getOrder();
//        return ResponseEntity.ok(orderDetailDto);
        ResOrderDetailDto orderDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

