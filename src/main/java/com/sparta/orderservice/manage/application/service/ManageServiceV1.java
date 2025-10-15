package com.sparta.orderservice.manage.application.service;

import com.sparta.orderservice.manage.domain.repository.ManageRepository;
import com.sparta.orderservice.manage.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageServiceV1 {

    private ManageRepository manageRepository;

    /* 회원 조회 */
    public List<ResUserDtoV1> getUserList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        List<ResUserDtoV1> userDtoList = new ArrayList<>();
        userDtoList.add(new ResUserDtoV1("456", "무도사", "moo@naver.com","광주광역시","비활성화"));

        return userDtoList;
    }

    /* 회원 상세 조회 */
    public ResUserDetailDtoV1 getUser(Integer userId) {
        ResUserDetailDtoV1 userDetailDto = ResUserDetailDtoV1.builder()
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
        return userDetailDto;
    }

    /* 회원 비활성화 */
    public void userDeactive(Integer userId) {}

    /* 회원 활성화 */
    public void userActive(Integer userId) {}

    /* 가게 조회 */
    public List<ResStoreDtoV1> getStoreList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        List<ResStoreDtoV1> storeDtoList = new ArrayList<>();

        storeDtoList.add(new ResStoreDtoV1("S001", "맛집1", "한식", "서울시 종로구", "활성화"));
        storeDtoList.add(new ResStoreDtoV1("S002", "맛집2", "중식", "서울시 강남구", "비활성화"));

        return storeDtoList;
    }

    /* 가게 상세 조회 */
    public ResStoreDetailDtoV1 getStore(Integer storeId) {
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

        return storeDetail;
    }

    /* 가게 비활성화 */
    public void storeDeactive(Integer storeId) {}

    /* 가게 활성화 */
    public void storeActive(Integer storeId) {}

    /* 주문 리스트 조회 */
    public List<ResOrderDtoV1> getOrderList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
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

        return orderDtoList;
    }

    /* 주문 상세 조회 */
    public ResOrderDetailDtoV1 getOrder(Integer orderId) {
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
                .createBy("user1")
                .deleteBy(null)
                .build();
        return orderDetail;
    }
}
