package com.sparta.orderservice.payment.application;

import com.sparta.orderservice.payment.domain.entity.Payment;
import com.sparta.orderservice.payment.domain.entity.PaymentMethodEnum;
import com.sparta.orderservice.payment.domain.entity.PaymentStatusEnum;
import com.sparta.orderservice.payment.domain.repository.PaymentRepository;
import com.sparta.orderservice.payment.presentation.advice.PaymentErrorCode;
import com.sparta.orderservice.payment.presentation.advice.PaymentException;
import com.sparta.orderservice.payment.presentation.advice.PaymentExceptionLogUtils;
import com.sparta.orderservice.payment.presentation.dto.request.ReqPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceV1 {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final Long USER_ID = null;

    public ResPaymentDtoV1 completePayment(ReqPaymentDtoV1 request) {

        User user = userRepository.findById(USER_ID).orElseThrow();

        Payment payment = Payment.ofNewPayment(
                PaymentMethodEnum.fromDisplayName(request.getPayType()),
                request.getAmount(),
                PaymentStatusEnum.PAID,
                null,
                user
        );

        paymentRepository.save(payment);

        return convertResPaymentDto(payment, user.getName());
    }

    @Transactional(readOnly = true)
    public Page<ResPaymentSummaryDtoV1> getPaymentPage(Pageable pageable) {
        return paymentRepository.findPaymentListByUserId(USER_ID, pageable);
    }

    @Transactional(readOnly = true)
    public ResPaymentDtoV1 getPayment(UUID paymentId) {
        return paymentRepository.findPaymentByUserId(paymentId, USER_ID)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        PaymentExceptionLogUtils.getNotFoundMessage(paymentId, USER_ID)
                ));
    }

    public void cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        PaymentExceptionLogUtils.getNotFoundMessage(paymentId, USER_ID)
                ));

        payment.cancel(USER_ID);
    }

    private ResPaymentDtoV1 convertResPaymentDto(Payment payment, String userName) {
        return new ResPaymentDtoV1(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getAmount(),
                payment.getMethod().getDisplayName(),
                userName,
                payment.getStatus().getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }

}
