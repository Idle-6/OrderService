package com.sparta.orderservice.payment.domain.repository.impl;

import com.sparta.orderservice.global.infrastructure.querydsl.QuerydslConfig;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
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

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    TestEntityManager manager;

    Payment payment;
    User user;
    Store store;
    Order order;
    @BeforeEach
    void setUp() {
        User owner = User.builder().email("owner@test.com").password("password").name("가게주인").address("서울 강남구").role(UserRoleEnum.OWNER).isActive(true).build();
        user = User.builder().email("user@test.com").password("password").name("이영희").address("서울 마포구").role(UserRoleEnum.USER).isActive(true).build();
        manager.persist(owner);
        manager.persist(user);
        manager.flush();

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, null, owner);
        manager.persistAndFlush(store);

        order = Order.ofNewOrder(user, store, 100000, "주문 대기", user);
        manager.persistAndFlush(order);

        payment = Payment.ofNewPayment(PaymentMethodEnum.CASH, 100000, PaymentStatusEnum.PAID, "9bd783a44bf24b7b96e4f72c3f1a1234", order, user);
        manager.persistAndFlush(payment);

        manager.clear();
    }

    @Test
    @DisplayName("결제 리스트 조회")
    void findPaymentPageByUserId() {
        Page<ResPaymentSummaryDtoV1> response = paymentRepository.findPaymentPageByUserId(user.getUserId(), Pageable.ofSize(5));

        assertNotNull(response.getContent());

        assertAll(() -> {
            assertEquals(1, response.getTotalElements());
            assertEquals(payment.getPaymentId(), response.getContent().get(0).getPaymentId());
            assertEquals(payment.getAmount(), response.getContent().get(0).getAmount());
            assertEquals(payment.getStatus(), response.getContent().get(0).getStatus());
        });

    }

    @Test
    @DisplayName("결제 상세 조회")
    void findPaymentById() {
        Optional<ResPaymentDtoV1> response = paymentRepository.findPaymentById(payment.getPaymentId());

        assertTrue(response.isPresent());

        assertAll(() -> {
            assertEquals(payment.getPaymentId(), response.get().getPaymentId());
            assertEquals(order.getOrderId(), response.get().getOrderId());
            assertEquals(payment.getAmount(), response.get().getAmount());
            assertEquals(payment.getMethod(), response.get().getPayType());
            assertEquals(user.getName(), response.get().getUserName());
            assertEquals(payment.getStatus(), response.get().getStatus());
        });
    }

    @Test
    @DisplayName("결제 취소")
    void cancel() {
        payment.cancel(user.getUserId());

        assertNotNull(payment.getDeletedAt());
        assertEquals(user.getUserId(), payment.getDeletedBy());

    }
}