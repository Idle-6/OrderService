package com.sparta.orderservice.payment.domain.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.order.domain.entity.QOrder;
import com.sparta.orderservice.payment.domain.entity.QPayment;
import com.sparta.orderservice.payment.domain.repository.CustomPaymentRepository;
import com.sparta.orderservice.payment.presentation.dto.SearchParam;
import com.sparta.orderservice.payment.presentation.dto.response.*;
import com.sparta.orderservice.user.domain.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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
    public Page<ResManagerPaymentDtoV1> findPaymentPage(SearchParam searchParam, Pageable pageable) {
        BooleanBuilder where = buildWhere(null, null, searchParam);
        return queryPayments(where, pageable, ResManagerPaymentDtoV1.class);
    }

    @Override
    public Page<ResStorePaymentDtoV1> findPaymentPageByStoreId(UUID storeId, SearchParam searchParam, Pageable pageable) {
        BooleanBuilder where = buildWhere(null, storeId, searchParam);
        return queryPayments(where, pageable, ResStorePaymentDtoV1.class);
    }

    @Override
    public Page<ResPaymentSummaryDtoV1> findPaymentPageByUserId(Long userId, SearchParam searchParam, Pageable pageable) {
        BooleanBuilder where = buildWhere(userId, null, searchParam);
        return queryPayments(where, pageable, ResPaymentSummaryDtoV1.class);
    }

    private <T> Page<T> queryPayments(BooleanBuilder where, Pageable pageable, Class<T> dtoClass) {
        int pageSize = pageable.getPageSize();
        if (!List.of(10, 30, 50).contains(pageSize)) {
            pageSize = 10;
        }
        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        Expression<T> projection = getProjection(dtoClass);

        JPAQuery<T> jpaQuery = query.select(projection)
                .from(qPayment)
                .where(where)
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize())
                .orderBy(qPayment.createdAt.desc());

        JPAQuery<Long> countQuery = query.select(qPayment.count())
                .from(qPayment)
                .where(where);

        List<T> resultList = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(resultList, adjustedPageable, countQuery::fetchOne);
    }

    @SuppressWarnings("unchecked")
    private <T> Expression<T> getProjection(Class<T> dtoClass) {
        if (dtoClass == ResManagerPaymentDtoV1.class) {
            return (Expression<T>) new QResManagerPaymentDtoV1(
                    qPayment.paymentId,
                    qPayment.amount,
                    qPayment.status,
                    qPayment.createdAt,
                    qPayment.method,
                    qPayment.user.name,
                    qPayment.order.store.name,
                    qPayment.createdAt,
                    qPayment.updatedAt
            );
        }
        if (dtoClass == ResStorePaymentDtoV1.class) {
            return (Expression<T>) new QResStorePaymentDtoV1(
                    qPayment.paymentId,
                    qPayment.amount,
                    qPayment.status,
                    qPayment.createdAt,
                    qPayment.method,
                    qPayment.user.name,
                    qPayment.createdAt
            );
        }
        if (dtoClass == ResPaymentSummaryDtoV1.class) {
            return (Expression<T>) new QResPaymentSummaryDtoV1(
                    qPayment.paymentId,
                    qPayment.amount,
                    qPayment.status,
                    qPayment.createdAt
            );
        }
        throw new IllegalArgumentException("Unsupported DTO class: " + dtoClass.getName());
    }

    private BooleanBuilder buildWhere(Long userId, UUID storeId, SearchParam searchParam) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(userExpression(userId))
                .and(storeExpression(storeId))
                .and(searchExpression(searchParam));
        return builder;
    }

    private BooleanBuilder userExpression(Long userId) {
        BooleanBuilder builder = new BooleanBuilder();
        if (userId != null) {
            builder.and(qPayment.user.userId.eq(userId));
        }
        return builder;
    }

    private BooleanBuilder storeExpression(UUID storeId) {
        BooleanBuilder builder = new BooleanBuilder();
        if (storeId != null) {
            builder.and(qPayment.order.store.storeId.eq(storeId));
        }
        return builder;
    }

    private BooleanBuilder searchExpression(SearchParam searchParam) {
        BooleanBuilder builder = new BooleanBuilder();

        if (searchParam != null && searchParam.getTerm() != null && !searchParam.getTerm().isBlank()) {
            String term = searchParam.getTerm();

            BooleanExpression userNameCond = qPayment.user.name.containsIgnoreCase(term);
            BooleanExpression storeNameCond = qPayment.order.store.name.containsIgnoreCase(term);

            BooleanExpression orderIdCond = tryParseUUID(term)
                    .map(uuid -> qPayment.order.orderId.eq(uuid))
                    .orElse(null);

            BooleanBuilder termBuilder = new BooleanBuilder();
            termBuilder.or(userNameCond)
                    .or(storeNameCond);

            if (orderIdCond != null) {
                termBuilder.or(orderIdCond);
            }

            builder.and(termBuilder);
        }

        return builder;
    }

    private Optional<UUID> tryParseUUID(String str) {
        try {
            return Optional.of(UUID.fromString(str));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }


    @Override
    public Optional<ResPaymentDtoV1> findPaymentById(UUID paymentId) {
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
                .where(qPayment.paymentId.eq(paymentId))
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
