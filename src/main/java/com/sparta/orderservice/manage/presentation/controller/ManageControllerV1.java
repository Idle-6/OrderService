package com.sparta.orderservice.manage.presentation.controller;

import com.sparta.orderservice.manage.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manages")
public class ManageControllerV1 {

    // private final ManageServise manageService;

    /* 회원 조회 */
    @GetMapping("/customers")
    public ResponseEntity<List<ResUserDtoV1>> getUserList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc
    ){
        List<ResUserDtoV1> customerDtoList = new ArrayList<>();

        customerDtoList.add(new ResUserDtoV1("123", "배추도사", "beachoo@naver.com", "서울특별시", "활성화"));
        customerDtoList.add(new ResUserDtoV1("456", "무도사", "moo@naver.com","광주광역시","비활성화"));
        return ResponseEntity.ok(customerDtoList);
    }

    /* 회원 상세 조회 */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResUserDetailDtoV1> getUser(
            @PathVariable Integer userId
    ){
        ResUserDetailDtoV1 customerDetailDto = ResUserDetailDtoV1.builder()
                .userId(userId.toString())
                .name("홍길동")
                .email("hong@test.com")
                .address("서울특별시 강남구")
                .status("활성화")
                .orderMenuList(List.of(
                        new orderDtoV1("123"),
                        new orderDtoV1("456")
                ))
                .createdAt(LocalDateTime.now().minusDays(10))
                .updateAt(LocalDateTime.now())
                .createBy("admin")
                .updateBy("manager")
                .build();

        return ResponseEntity.ok(customerDetailDto);
    }


    /* 회원 비활성화 */
    @PostMapping("/users/{userId}/deactive")
    public ResponseEntity<?> userDeactive(
            @PathVariable Integer userId
    ){
//        manageService.customerDeactive();
//        return ResponseEntity.ok("회원 비활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 회원 활성화 */
    @PostMapping("/users/{userId}/active")
    public ResponseEntity<?> userActive(
            @PathVariable Integer userId
    ){
//        manageService.customerActive();
//        return ResponseEntity.ok("회원 활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 조회 */
    @GetMapping("/store-list")
    public ResponseEntity<List<ResStoreDtoV1>> getStoreList(
            @RequestParam String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean isAsc){

        List<ResStoreDtoV1> storeDtoList = new ArrayList<>();

        storeDtoList.add(new ResStoreDtoV1("S001", "맛집1", "한식", "서울시 종로구", "활성화"));
        storeDtoList.add(new ResStoreDtoV1("S002", "맛집2", "중식", "서울시 강남구", "비활성화"));

        return ResponseEntity.ok(storeDtoList);
    }

    /* 가게 상세 조회 */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> getStore(
            @PathVariable Integer storeId
    ){
        ResStoreDetailDtoV1 storeDetail = ResStoreDetailDtoV1.builder()
                .storeId(storeId.toString())
                .storeName("홍길동네식당")
                .category("한식")
                .contact("010-1234-5678")
                .address("서울특별시 마포구")
                .description("든든한 한식 메뉴 제공")
                .status("활성화")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updateAt(LocalDateTime.now())
                .createBy("admin")
                .updateBy("owner1")
                .build();

        return ResponseEntity.ok(storeDetail);
    }

    /* 가게 비활성화 */
    @PostMapping("/stores/{storeId}/deactive")
    public ResponseEntity<?> storeDeactive(
            @PathVariable Integer storeId
    ){
//        manageService.storeDeactive();
//        return ResponseEntity.ok("가게 비활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 가게 활성화 */
    @PostMapping("/stores/{storeId}/active")
    public ResponseEntity<?> storeActive(
            @PathVariable Integer storeId
    ){
//        manageService.storeActive();
//        return ResponseEntity.ok("가게 활성화 완료");
        return new ResponseEntity<>(HttpStatus.OK);
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
        List<ResOrderDtoV1> orderDtoList = new ArrayList<>();

        orderDtoList.add(ResOrderDtoV1.builder()
                .orderId("O1001")
                .customerId("123")
                .storeId("S001")
                .totalPrice(25000)
                .orderStatus("주문완료")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build()
        );

        orderDtoList.add(ResOrderDtoV1.builder()
                .orderId("O1002")
                .customerId("456")
                .storeId("S002")
                .totalPrice(18000)
                .orderStatus("배송중")
                .createdAt(LocalDateTime.now().minusHours(10))
                .build()
        );

        return ResponseEntity.ok(orderDtoList);
    }

    /* 주문 상세 조회 */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResOrderDetailDtoV1> getOrder(
            @PathVariable Integer orderId
    ){
        ResOrderDetailDtoV1 orderDetail = ResOrderDetailDtoV1.builder()
                .orderId(orderId.toString())
                .customerId("123")
                .storeId("S001")
                .oderMessage("문 앞에 두고 가주세요")
                .orderMenuList(List.of(
                        new orderMenuDtoV1("1001", "김치찌개", 2, 18000),
                        new orderMenuDtoV1("1002", "된장찌개", 1, 8000)
                ))
                .totalPrice(26000)
                .orderStatus("주문완료")
                .createdAt(LocalDateTime.now().minusDays(2))
                .deleteAt(null)
                .createBy("customer1")
                .deleteBy(null)
                .build();

        return ResponseEntity.ok(orderDetail);
    }
}

