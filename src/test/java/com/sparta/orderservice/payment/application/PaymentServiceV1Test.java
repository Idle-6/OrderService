package com.sparta.orderservice.payment.application;

import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.advice.PaymentException;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceV1Test {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    PaymentServiceV1 paymentService;

    User user, owner;
    Store store;
    Order order;
    @BeforeEach
    void setUp() {
        owner = User.builder().email("owner@test.com").password("password").name("가게주인").address("서울 강남구").role(UserRoleEnum.OWNER).isActive(true).build();
        ReflectionTestUtils.setField(owner, "userId", 1L);

        user = User.builder().email("user@test.com").password("password").name("이영희").address("서울 마포구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(user, "userId", 2L);

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, null, owner);
        order = Order.ofNewOrder(user, store, 100000, "주문 대기", user);
        ReflectionTestUtils.setField(order, "orderId", UUID.randomUUID());

    }

    @Test
    @DisplayName("결제 완료")
    void completePayment() {
        ReqPaymentDtoV1 request = new ReqPaymentDtoV1(UUID.randomUUID(), 100000, PaymentMethodEnum.MOBILE_PAY, null, null, null, null);
        ReflectionTestUtils.setField(order, "orderId", UUID.randomUUID());
        when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));

        paymentService.completePayment(request, user);

        verify(orderRepository, Mockito.times(1)).findById(Mockito.any());
        verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
    }

    @Test
    @DisplayName("결제 조회 - 페이징")
    void getPaymentPage() {
        ResPaymentSummaryDtoV1 response = new ResPaymentSummaryDtoV1(UUID.randomUUID(), 100000, PaymentStatusEnum.PAID, LocalDateTime.now());
        when(paymentRepository.findPaymentListByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(new PageImpl<>(List.of(response)));

        paymentService.getPaymentPage(Pageable.ofSize(5), user.getUserId());

        verify(paymentRepository, Mockito.times(1)).findPaymentListByUserId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @DisplayName("결제 조회 - 상세")
    void getPayment() {
        UUID paymentId = UUID.randomUUID();
        ResPaymentDtoV1 response = new ResPaymentDtoV1(paymentId, order.getOrderId(), 100000, PaymentMethodEnum.MOBILE_PAY, user.getName(), PaymentStatusEnum.PAID, LocalDateTime.now(), null, null);
        when(paymentRepository.findPaymentByUserId(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(response));

        paymentService.getPayment(paymentId, user.getUserId());

        verify(paymentRepository, Mockito.times(1)).findPaymentByUserId(Mockito.any(), Mockito.anyLong());
    }

    @Test
    @DisplayName("결제 조회 - 존재하지 않음")
    void getPayment_not_found() {
        when(paymentRepository.findPaymentByUserId(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(PaymentException.class, () -> paymentService.getPayment(UUID.randomUUID(), user.getUserId()));
    }

    @Test
    @DisplayName("결제 취소 - 주문자")
    void cancelPayment() {
        Payment payment = Payment.ofNewPayment(PaymentMethodEnum.CASH, 100000, PaymentStatusEnum.PAID, order, user);
        ReflectionTestUtils.setField(payment, "paymentId", UUID.randomUUID());
        when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));

        paymentService.cancelPayment(payment.getPaymentId(), user);

        verify(paymentRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("결제 취소 - 가게 주인")
    void cancelPayment_store() {
        Payment payment = Payment.ofNewPayment(PaymentMethodEnum.CASH, 100000, PaymentStatusEnum.PAID, order, user);
        ReflectionTestUtils.setField(payment, "paymentId", UUID.randomUUID());
        when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));

        paymentService.cancelPayment(payment.getPaymentId(), owner);

        verify(paymentRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("결제 취소 - 관리자")
    void cancelPayment_admin() {
        Payment payment = Payment.ofNewPayment(PaymentMethodEnum.CASH, 100000, PaymentStatusEnum.PAID, order, user);
        ReflectionTestUtils.setField(payment, "paymentId", UUID.randomUUID());

        User admin = User.builder().email("admin@test.com").password("password").name("관리자").address("서울 강남구").role(UserRoleEnum.ADMIN).isActive(true).build();
        ReflectionTestUtils.setField(admin, "userId", 3L);

        when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));

        paymentService.cancelPayment(payment.getPaymentId(), admin);

        verify(paymentRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("결제 취소 - 권한없음")
    void cancelPayment_forbidden() {
        Payment payment = Payment.ofNewPayment(PaymentMethodEnum.CASH, 100000, PaymentStatusEnum.PAID, order, user);
        ReflectionTestUtils.setField(payment, "paymentId", UUID.randomUUID());

        User user = User.builder().email("user@test.com").password("password").name("user").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(user, "userId", 4L);

        when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));

        assertThrows(PaymentException.class, () -> paymentService.cancelPayment(payment.getPaymentId(), user));

    }
}