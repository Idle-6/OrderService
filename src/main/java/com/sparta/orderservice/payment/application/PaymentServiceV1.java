package com.sparta.orderservice.payment.application;

import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.order.presentation.advice.OrderErrorCode;
import com.sparta.orderservice.order.presentation.advice.OrderException;
import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.advice.PaymentErrorCode;
import com.sparta.orderservice.payment.presentation.advice.PaymentException;
import com.sparta.orderservice.payment.presentation.advice.PaymentExceptionLogUtils;
import com.sparta.orderservice.payment.presentation.dto.SearchParam;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentUpdateDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResManagerPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResStorePaymentDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.store.presentation.advice.StoreErrorCode;
import com.sparta.orderservice.store.presentation.advice.StoreException;
import com.sparta.orderservice.store.presentation.advice.StoreExceptionLogUtils;
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
    private final StoreRepository storeRepository;

    public ResPaymentDtoV1 completePayment(ReqPaymentDtoV1 request, User user) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

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
    public Page<ResPaymentSummaryDtoV1> getPaymentPage(Pageable pageable, SearchParam searchParam, Long userId) {
        return paymentRepository.findPaymentPageByUserId(userId, searchParam, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResManagerPaymentDtoV1> getPaymentPageForManager(SearchParam searchParam, Pageable pageable) {
        return paymentRepository.findPaymentPage(searchParam, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResStorePaymentDtoV1> getPaymentPageForOwner(User user, UUID storeId, SearchParam searchParam, Pageable pageable) {

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND, StoreExceptionLogUtils.getNotFoundMessage(storeId, user.getUserId())));

        if(!store.getCreatedBy().getUserId().equals(user.getUserId())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_VIEW_FORBIDDEN, PaymentExceptionLogUtils.getViewForbiddenMessage(storeId, user.getUserId()));
        }

        return paymentRepository.findPaymentPageByStoreId(storeId, searchParam, pageable);
    }

    @Transactional(readOnly = true)
    public ResPaymentDtoV1 getPayment(UUID paymentId, Long userId) {
        return paymentRepository.findPaymentById(paymentId)
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

        // 외부 결제 시스템에 취소 요청
//        pgPaymentClient.cancelPayment(payment.getTransactionId(), payment.getAmount());

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
