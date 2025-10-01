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
    public ResponseEntity<?> getCustomerList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc
    ){
//        List<ResCustomerDto> customerDtoList = manageService.getCustomerList();
//        return new ResponseEntity<>(customerDtoList, HttpStatus.OK);
        List<ResCustomerDto> customerDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 회원 상세 조회 */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomer(
            @PathVariable Integer customerId
    ){
//        ResCustomerDetailDto customerDetailDto = manageService.getCustomerDetail();
//        return new ResponseEntity<>(customerDetailDto, HttpStatus.OK);
        ResCustomerDetailDto customerDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /* 회원 비활성화 */
    @PostMapping("/customers/{customerId}/deactive")
    public ResponseEntity<?> customerDeactive(
            @PathVariable Integer customerId
    ){
//        manageService.customerDeactive();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 회원 활성화 */
    @PostMapping("/customers/{customerId}/active")
    public ResponseEntity<?> customerActive(
            @PathVariable Integer customerId
    ){
//        manageService.customerActive();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 조회 */
    @GetMapping("/store-list/{search}")
    public ResponseEntity<?> getStoreList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){
//        List<ResStoreDto> storeDtoList = manageService.getStoreList();
//        return new ResponseEntity<>(storeDtoList, HttpStatus.OK);
        List<ResStoreDto> storeDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 상세 조회 */
    @GetMapping("/stores/{ownerId}")
    public ResponseEntity<?> getStore(
            @PathVariable Integer ownerId
    ){
//        ResStoreDetailDto storeDetailDto = manageService.getStore();
//        return new ResponseEntity<>(storeDetailDto, HttpStatus.OK);
        ResStoreDetailDto storeDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 비활성화 */
    @PostMapping("/stores/{ownerId}/deactive")
    public ResponseEntity<?> storeDeactive(
            @PathVariable Integer ownerId
    ){
//        manageService.storeDeactive();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 활성화 */
    @PostMapping("/stores/{ownerId}/active")
    public ResponseEntity<?> storeActive(
            @PathVariable Integer ownerId
    ){
//        manageService.storeActive();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 주문 리스트 조회 */
    @GetMapping("/order-list/{search}")
    public ResponseEntity<?> getOrderList(
            @PathVariable String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){
//        List<ResOrderDto> orderDtoList = manageService.getOrderList();
//        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
        List<ResOrderDto> orderDtoList;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 주문 상세 조회 */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable Integer orderId
    ){
//        ResOrderDetailDto orderDetailDto = manageService.getOrder();
//        return new ResponseEntity<>(orderDetailDto, HttpStatus.OK);
        ResOrderDetailDto orderDetailDto;
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

