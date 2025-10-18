package com.sparta.orderservice.payment.domain.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.order.domain.entity.QOrder;
import com.sparta.orderservice.payment.domain.entity.QPayment;
import com.sparta.orderservice.payment.domain.repository.CustomPaymentRepository;
import com.sparta.orderservice.payment.presentation.dto.response.QResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.QResPaymentSummaryDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentDtoV1;
import com.sparta.orderservice.payment.presentation.dto.response.ResPaymentSummaryDtoV1;
import com.sparta.orderservice.user.domain.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomPaymentRepositoryImpl implements CustomPaymentRepository {

    private final JPAQueryFactory query;

    QPayment qPayment = QPayment.payment;
    QOrder qOrder = QOrder.order;
    QUser qUser = QUser.user;

    @Override
    public Page<ResPaymentSummaryDtoV1> findPaymentPageByUserId(Long userId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<ResPaymentSummaryDtoV1> jpaQuery = query.select(
                        new QResPaymentSummaryDtoV1(
                                qPayment.paymentId,
                                qPayment.amount,
                                qPayment.status,
                                qPayment.createdAt
                        ))
                .from(qPayment)
                .leftJoin(qOrder).on(qPayment.order.eq(qOrder))
                .leftJoin(qUser).on(qPayment.user.eq(qUser))
                .where(qUser.userId.eq(userId))
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        JPAQuery<Long> count = query.select(qPayment.count())
                .from(qPayment)
                .leftJoin(qOrder).on(qPayment.order.eq(qOrder))
                .leftJoin(qUser).on(qPayment.user.eq(qUser))
                .where(qUser.userId.eq(userId));

        List<ResPaymentSummaryDtoV1> resultList = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(resultList, adjustedPageable, count::fetchOne);
    }

    @Override
    public Optional<ResPaymentDtoV1> findPaymentByUserId(UUID paymentId, Long userId) {
        ResPaymentDtoV1 result = query.select(
                        new QResPaymentDtoV1(
                                qPayment.paymentId,
                                qPayment.order.orderId,
                                qPayment.amount,
                                qPayment.method,
                                qPayment.user.name,
                                qPayment.status,
                                qPayment.createdAt,
                                qPayment.updatedAt,
                                qPayment.deletedAt

                        ))
                .from(qPayment)
                .leftJoin(qOrder).on(qPayment.order.eq(qOrder))
                .leftJoin(qUser).on(qPayment.user.eq(qUser))
                .where(qUser.userId.eq(userId), qPayment.paymentId.eq(paymentId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<ResPaymentDtoV1> findPaymentByOrderId(UUID orderId, Long userId) {
        ResPaymentDtoV1 result = query.select(
                        new QResPaymentDtoV1(
                                qPayment.paymentId,
                                qPayment.order.orderId,
                                qPayment.amount,
                                qPayment.method,
                                qPayment.user.name,
                                qPayment.status,
                                qPayment.createdAt,
                                qPayment.updatedAt,
                                qPayment.deletedAt

                        ))
                .from(qPayment)
                .leftJoin(qOrder).on(qPayment.order.eq(qOrder))
                .leftJoin(qUser).on(qPayment.user.eq(qUser))
                .where(qUser.userId.eq(userId), qPayment.order.orderId.eq(orderId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
