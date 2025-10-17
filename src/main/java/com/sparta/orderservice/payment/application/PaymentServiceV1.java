package com.sparta.orderservice.payment.application;

import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.advice.PaymentErrorCode;
import com.sparta.orderservice.payment.presentation.advice.PaymentException;
import com.sparta.orderservice.payment.presentation.advice.PaymentExceptionLogUtils;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentUpdateDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceV1 {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public ResPaymentDtoV1 completePayment(ReqPaymentDtoV1 request, User user) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        Payment payment = Payment.ofNewPayment(
                request.getPayType(),
                request.getAmount(),
                PaymentStatusEnum.PAID,
                request.getPgToken(),
                order,
                user
        );

        paymentRepository.save(payment);

        return convertResPaymentDto(payment, user.getName());
    }

    @Transactional(readOnly = true)
    public Page<ResPaymentSummaryDtoV1> getPaymentPage(Pageable pageable, Long userId) {
        return paymentRepository.findPaymentPageByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public ResPaymentDtoV1 getPayment(UUID paymentId, Long userId) {
        return paymentRepository.findPaymentByUserId(paymentId, userId)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        PaymentExceptionLogUtils.getNotFoundMessage(paymentId, userId)
                ));
    }

    public void updatePaymentStatus(UUID paymentId, ReqPaymentUpdateDtoV1 request, User user) {

        Long userId = user.getUserId();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(
                PaymentErrorCode.PAYMENT_NOT_FOUND,
                PaymentExceptionLogUtils.getNotFoundMessage(paymentId, userId)
        ));

        boolean isStoreOwner = Objects.equals(payment.getOrder().getStore().getCreatedBy().getUserId(), user.getUserId());
        boolean isAdmin = Objects.equals(user.getRole(),UserRoleEnum.ADMIN);

        if(!isStoreOwner && !isAdmin) {
            throw new PaymentException(
                    PaymentErrorCode.PAYMENT_UPDATE_STATUS_FORBIDDEN,
                    PaymentExceptionLogUtils.getUpdateStatusMessage(paymentId, userId)
            );
        }

        payment.updateStatus(request.getStatus(), userId);
    }

    public void cancelPayment(UUID paymentId, User user) {
        Long userId = user.getUserId();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        PaymentExceptionLogUtils.getNotFoundMessage(paymentId, userId)
                ));

        boolean isStoreOwner = Objects.equals(payment.getOrder().getStore().getCreatedBy().getUserId(), user.getUserId());
        boolean isAdmin = Objects.equals(user.getRole(), UserRoleEnum.ADMIN);
        boolean isOrderOwner = Objects.equals(payment.getOrder().getUser().getUserId(), user.getUserId());

        if(!isStoreOwner && !isAdmin && !isOrderOwner) {
            throw new PaymentException(
                    PaymentErrorCode.PAYMENT_CANCEL_FORBIDDEN,
                    PaymentExceptionLogUtils.getCancelMessage(paymentId, userId)
            );
        }

        payment.cancel(userId);
    }

    private ResPaymentDtoV1 convertResPaymentDto(Payment payment, String userName) {
        return new ResPaymentDtoV1(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                userName,
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }

}
