package com.sparta.orderservice.order.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.menu.domain.repository.MenuRepository;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.domain.repository.OrderMenuRepository;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.store.application.service.StoreServiceV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
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

    @Mock
    MenuRepository menuRepository;

    @Mock
    UserRepository  userRepository;

    @Mock
    StoreRepository storeRepository;

    User admin;
    Category category;
    Store store;
    Order order;
    MenuEntity menu1, menu2;
    UUID orderId, storeId, categoryId, menuId1, menuId2;
    ReqOrderMenuDtoV1 menuDto1, menuDto2;
    List<ReqOrderMenuDtoV1> orderMenus;

    @BeforeEach
    void setUp() {
        admin = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(admin, "userId", 1L);

        category = Category.ofNewCategory("한식", admin.getUserId());
        categoryId = UUID.randomUUID();
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category, admin);
        storeId = UUID.randomUUID();
        ReflectionTestUtils.setField(store, "storeId", storeId);

        order = Order.ofNewOrder(admin, store, 15000, "문 앞에 두고 가주세요", admin);
        orderId = UUID.randomUUID();
        ReflectionTestUtils.setField(order, "orderId", orderId);

        menuId1 = UUID.randomUUID();
        menu1 = MenuEntity.builder()
                .id(menuId1)
                .name("불고기버거")
                .description("맛있는 불고기버거")
                .price(5000)
                .isPublic(true)
                .storeId(store.getStoreId())
                .build();

        menuId2 = UUID.randomUUID();
        menu2 = MenuEntity.builder()
                .id(menuId1)
                .name("치즈버거")
                .description("더 맛있는 치즈버거")
                .price(6000)
                .isPublic(true)
                .storeId(store.getStoreId())
                .build();

        menuDto1 = new ReqOrderMenuDtoV1(menuId1, 2);
        menuDto2 = new ReqOrderMenuDtoV1(menuId2, 1);
        orderMenus = List.of(menuDto1, menuDto2);

    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() {

        when(menuRepository.findById(menuId1)).thenReturn(Optional.of(menu1));
        when(menuRepository.findById(menuId2)).thenReturn(Optional.of(menu2));
        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        ReqOrderDtoV1 request = new ReqOrderDtoV1(0, "배고파요", OrderStatus.CREATED, store, admin, orderMenus);

        ResOrderDtoV1 response = orderServiceV1.createOrder(request);

        verify(menuRepository, Mockito.times(1)).findById(menuId1);
        verify(menuRepository, Mockito.times(1)).findById(menuId2);
        verify(userRepository, Mockito.times(1)).findById(admin.getUserId());
        verify(storeRepository, Mockito.times(1)).findById(storeId);
        verify(orderRepository, Mockito.times(1)).save(Mockito.any(Order.class));

        System.out.println("총 가격: " + response.getTotalPrice());
    }

    @Test
    @DisplayName("주문 조회 - 페이징")
    void getOrderPage() {
        SearchParam searchParam = new SearchParam();
        ResOrderDtoV1 response = new ResOrderDtoV1(UUID.randomUUID(), 15000, OrderStatus.COMPLETE, store.getName(), store.getDescription(), LocalDateTime.now());
        when(orderRepository.findOrderPage(Mockito.any(SearchParam.class), Mockito.any())).thenReturn(new PageImpl<>(List.of(response)));

        orderServiceV1.getOrders(searchParam, Pageable.ofSize(5));

        verify(orderRepository, Mockito.times(1)).findOrderPage(Mockito.any(SearchParam.class), Mockito.any());

    }

    @Test
    @DisplayName("주문 조회")
    void getOrder() {
        UUID orderId = UUID.randomUUID();
        ResOrderDetailDtoV1 response = new ResOrderDetailDtoV1(orderId, "배고파서 현기증 나요", 30000, OrderStatus.START, store.getName(), store.getDescription(), orderMenus, LocalDateTime.now(), LocalDateTime.now());
        when(orderRepository.findOrderDetailById(orderId)).thenReturn(Optional.of(response));

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
