package com.sparta.orderservice.order.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.domain.repository.OrderMenuRepository;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceV1Test {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceV1 orderServiceV1;

    User admin;
    Category category;
    Store store;
    Order order;
    UUID orderId, storeId;

    @BeforeEach
    void setUp() {
        admin = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(admin, "userId", 1L);

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category, admin);
        storeId = UUID.randomUUID();
        ReflectionTestUtils.setField(store, "storeId", storeId);

        order = Order.ofNewOrder(admin, store, 15000, "문 앞에 두고 가주세요", admin);
        orderId = UUID.randomUUID();
        ReflectionTestUtils.setField(order, "orderId", orderId);

    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        ReqOrderDtoV1 request = new ReqOrderDtoV1(50000, "배고파요", OrderStatus.CREATED, store, admin);
        orderServiceV1.createOrder(request);

        verify(orderRepository, Mockito.times(1)).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("주문 조회 - 페이징")
    void getOrderPage() {
        SearchParam searchParam = new SearchParam();
        ResOrderDtoV1 response = new ResOrderDtoV1(UUID.randomUUID(), admin.getUserId(), store.getStoreId(), 15000, OrderStatus.COMPLETE, LocalDateTime.now());
        when(orderRepository.findOrderPage(Mockito.any(SearchParam.class), Mockito.any())).thenReturn(new PageImpl<>(List.of(response)));

        orderServiceV1.getOrders(searchParam, Pageable.ofSize(5));

        verify(orderRepository, Mockito.times(1)).findOrderPage(Mockito.any(SearchParam.class), Mockito.any());

    }

    @Test
    @DisplayName("주문 조회")
    void getOrder() {
        UUID orderId = UUID.randomUUID();
        ResOrderDetailDtoV1 response = new ResOrderDetailDtoV1(orderId, admin.getUserId(), store.getStoreId(), "배고파서 현기증 나요", 30000, OrderStatus.START, LocalDateTime.now(), LocalDateTime.now());
        when(orderRepository.findOrderDetailById(Mockito.any())).thenReturn(Optional.of(response));

        orderServiceV1.getOrderDetail(orderId);

        verify(orderRepository, Mockito.times(1)).findOrderDetailById(Mockito.any());
    }

    @Test
    @DisplayName("주문 상태 변경")
    void updateStore() {
        when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));

        orderServiceV1.updateOrderStatus(orderId, OrderStatus.ACCEPTED);

        verify(orderRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("주문 취소")
    void deleteStore() {
        when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));

        orderServiceV1.cancelOrder(orderId);

        verify(orderRepository, Mockito.times(1)).findById(Mockito.any());
    }

}
