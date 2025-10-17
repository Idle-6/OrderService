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
import com.sparta.orderservice.payment.application.PaymentServiceV1;
import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceV1Test {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceV1 orderServiceV1;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentServiceV1 paymentService;

    @Mock
    MenuRepository menuRepository;

    @Mock
    UserRepository  userRepository;

    @Mock
    StoreRepository storeRepository;

    User user;
    Category category;
    Store store;
    Order order;
    MenuEntity menu1, menu2;
    UUID orderId, storeId, categoryId, menuId1, menuId2, paymentId;
    ReqOrderMenuDtoV1 menuDto1, menuDto2;
    List<ReqOrderMenuDtoV1> orderMenus;
    Payment payment;

    @BeforeEach
    void setUp() {
        user = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(user, "userId", 1L);

        category = Category.ofNewCategory("한식", user.getUserId());
        categoryId = UUID.randomUUID();
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category, user);
        storeId = UUID.randomUUID();
        ReflectionTestUtils.setField(store, "storeId", storeId);

        order = Order.ofNewOrder(user, store, 15000, "문 앞에 두고 가주세요", user);
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
        paymentId = UUID.randomUUID();
        payment = Payment.ofNewPayment(PaymentMethodEnum.CARD, 16000, PaymentStatusEnum.PAID, null, order, user);
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        ResPaymentDtoV1 paymentDto = new ResPaymentDtoV1(
                paymentId,
                orderId,
                5000,
                PaymentMethodEnum.CARD,
                user.getName(),
                PaymentStatusEnum.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(menuRepository.findById(menuId1)).thenReturn(Optional.of(menu1));
        when(menuRepository.findById(menuId2)).thenReturn(Optional.of(menu2));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(paymentService.completePayment(any(ReqPaymentDtoV1.class), eq(user))).thenReturn(paymentDto);

        ReqOrderDtoV1 request = new ReqOrderDtoV1(0, "배고파요", OrderStatus.CREATED, store, user, orderMenus);

        ResOrderDtoV1 response = orderServiceV1.createOrder(request, user);

        verify(menuRepository, Mockito.times(1)).findById(menuId1);
        verify(menuRepository, Mockito.times(1)).findById(menuId2);
        verify(storeRepository, Mockito.times(1)).findById(storeId);
        verify(orderRepository, Mockito.times(1)).save(Mockito.any(Order.class));
        verify(paymentService, times(1)).completePayment(any(ReqPaymentDtoV1.class), eq(user));

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
    @DisplayName("주문 상세 조회")
    void getOrder() {
        UUID orderId = UUID.randomUUID();
        ResOrderDetailDtoV1 response = new ResOrderDetailDtoV1(orderId, "배고파서 현기증 나요", 30000, OrderStatus.START, store.getName(), store.getDescription(), orderMenus, LocalDateTime.now(), LocalDateTime.now());
        when(orderRepository.findOrderDetailById(orderId)).thenReturn(Optional.of(response));

        orderServiceV1.getOrderDetail(orderId);

        verify(orderRepository, Mockito.times(1)).findOrderDetailById(Mockito.any());
    }

    @Test
    @DisplayName("주문 상태 변경")
    void updateOrder() {
        when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));

        orderServiceV1.updateOrderStatus(orderId, OrderStatus.ACCEPTED, user);

        verify(orderRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("주문 취소")
    void deleteOrder() {
        ResPaymentDtoV1 paymentDto = new ResPaymentDtoV1(
                paymentId,
                orderId,
                15000,
                PaymentMethodEnum.CARD,
                "testuser",
                PaymentStatusEnum.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(paymentRepository.findPaymentByOrderId(orderId, user.getUserId()))
                .thenReturn(Optional.of(paymentDto));

        doNothing().when(paymentService).cancelPayment(paymentId, user);

        orderServiceV1.cancelOrder(orderId, user);

        verify(orderRepository, times(1)).findById(any());
        verify(paymentService, times(1)).cancelPayment(paymentId, user);
    }

}
