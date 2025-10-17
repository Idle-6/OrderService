package com.sparta.orderservice.order.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.global.infrastructure.querydsl.QuerydslConfig;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("주문 레포지토리 테스트")
@Import(QuerydslConfig.class)
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    TestEntityManager manager;

    User user1;
    Category category1, category2;
    Store store1;
    Order order1, order2;

    @BeforeEach
    void setUp() {
        User admin = User.builder().email("admin@test.com").password("admin1").name("관리자").address("서울 강남구").role(UserRoleEnum.ADMIN).isActive(true).build();
        user1 = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        User user2 = User.builder().email("user2@test.com").password("password2").name("이영희").address("서울 마포구").role(UserRoleEnum.USER).isActive(true).build();
        manager.persist(admin);
        manager.persist(user1);
        manager.persist(user2);

        category1 = Category.ofNewCategory("한식", admin.getUserId());
        category2 = Category.ofNewCategory("중식", admin.getUserId());
        manager.persist(category1);
        manager.persist(category2);
        manager.flush();

        store1 = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category1, user1);
        Store store2 = Store.ofNewStore("가게이름2", "223-45-67890", "010-2222-2222", "서울 마포구 연남동", "시원한 한식", false, category1, user2);

        manager.persistAndFlush(store1);
        manager.persistAndFlush(store2);

        order1 = Order.ofNewOrder(user1, store1, 15000, "문 앞에 두고 가주세요", user1);
        order2 = Order.ofNewOrder(user2, store2, 30000, "빨리 보내주세요", user2);

        manager.persistAndFlush(order1);
        manager.persistAndFlush(order2);


    }

    @Test
    @DisplayName("주문 상태 변경")
    void updateOrderStatus() {
        order1.updateOrderStatus(OrderStatus.ACCEPTED, user1.getUserId());

        manager.flush();

        assertNotNull(order1.getUpdatedAt());
        assertEquals(user1.getUserId(), order1.getUpdatedBy());
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() {
        order1.cancelOrder(user1.getUserId());

        manager.flush();

        assertNotNull(order1.getUpdatedAt());
        assertEquals(user1.getUserId(), order1.getUpdatedBy());
    }

    @Test
    @DisplayName("주문 리스트 조회 - 전체")
    void findOrderPage() {
        SearchParam searchParam = new SearchParam();
        Page<ResOrderDtoV1> response = orderRepository.findOrderPage(searchParam, Pageable.ofSize(5));

        assertFalse(response.isEmpty());
        assertAll(() -> {
            assertEquals(2, response.getTotalElements());
            assertEquals(30000, response.getContent().get(0).getTotalPrice());
            assertEquals(15000, response.getContent().get(1).getTotalPrice());
        });

    }

    @Test
    @DisplayName("주문 리스트 조회 - 주문 상태별")
    void findOrderPage_orderStatus() {

        // 주문 상태별 조회 여부 확인하기 위해 상태 업데이트
        order1.updateOrderStatus(OrderStatus.ACCEPTED, user1.getUserId());
        manager.flush();
        
        SearchParam searchParam = new SearchParam(null, order1.getOrderStatus());
        Page<ResOrderDtoV1> response = orderRepository.findOrderPage(searchParam, Pageable.ofSize(5));

        assertFalse(response.isEmpty());

        assertAll(() -> {
            assertEquals(1, response.getTotalElements());
            assertEquals(OrderStatus.ACCEPTED,  response.getContent().get(0).getOrderStatus());
        });
    }

    @Test
    @DisplayName("주문 리스트 조회 - 검색")
    void findOrderPage_search() {
        // given
        SearchParam searchParam = new SearchParam(30000, null);
        Pageable pageable = Pageable.ofSize(5);

        // when
        Page<ResOrderDtoV1> response = orderRepository.findOrderPage(searchParam, pageable);

        // then
        assertFalse(response.isEmpty());
        assertAll(() -> {
            assertEquals(1, response.getTotalElements());
            assertEquals(30000, response.getContent().get(0).getTotalPrice());
        });
    }

    @Test
    @DisplayName("주문 상세 조회 - orderId")
    void findOrderDetailById() {
        Optional<ResOrderDetailDtoV1> response = orderRepository.findOrderDetailById(order1.getOrderId());

        assertTrue(response.isPresent());
        assertAll(() -> {
            assertEquals(order1.getOrderId(), response.get().getOrderId());
            assertEquals(order1.getTotalPrice(), response.get().getTotalPrice());
            assertEquals(order1.getOrderMessage(), response.get().getOrderMessage());
            assertEquals(OrderStatus.CREATED, response.get().getOrderStatus());
        });
    }

    @Test
    @DisplayName("주문 상세 조회 - userId")
    void findOrderDetailByUserId() {
        Optional<ResOrderDetailDtoV1> response = orderRepository.findOrderDetailByUserId(user1.getUserId());

        assertTrue(response.isPresent());
        assertAll(() -> {
            assertEquals(order1.getOrderId(), response.get().getOrderId());
            assertEquals(OrderStatus.CREATED, response.get().getOrderStatus());
        });

    }


}
