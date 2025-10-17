package com.sparta.orderservice.order.domain.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.QOrder;
import com.sparta.orderservice.order.domain.repository.CustomOrderRepository;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.order.presentation.dto.response.QResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.QResOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    private final JPAQueryFactory query;

    private static final String[] ALLOWED_SORT_PROPERTIES = {"totalPrice", "createdAt"};

    QOrder qOrder = QOrder.order;

    @Override
    public Page<ResOrderDtoV1> findOrderPage(SearchParam searchParam, Pageable pageable) {

        JPAQuery<ResOrderDtoV1> jpaQuery = query
                .select(getOrderProjection())
                .from(qOrder)
                .join(qOrder.store)
                .where(whereExpression(searchParam))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 처리
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                if (!List.of(ALLOWED_SORT_PROPERTIES).contains(order.getProperty())) continue;
                PathBuilder<Order> entityPath = new PathBuilder<>(Order.class, "order");
                jpaQuery.orderBy(new com.querydsl.core.types.OrderSpecifier(
                        order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                        entityPath.get(order.getProperty())
                ));
            }
        } else {
            jpaQuery.orderBy(qOrder.createdAt.desc());
        }

        // 총 개수
        JPAQuery<Long> countQuery = query
                .select(qOrder.count())
                .from(qOrder)
                .join(qOrder.store)
                .where(whereExpression(searchParam));

        List<ResOrderDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<ResOrderDetailDtoV1> findOrderDetailById(UUID orderId) {

        ResOrderDetailDtoV1 result = query
                .select(getOrderDetailProjection())
                .from(qOrder)
                .where(qOrder.orderId.eq(orderId))
                .join(qOrder.store)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<ResOrderDetailDtoV1> findOrderDetailByUserId(Long userId) {

        ResOrderDetailDtoV1 result = query
                .select(getOrderDetailProjection())
                .from(qOrder)
                .where(qOrder.user.userId.eq(userId))
                .join(qOrder.store)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanBuilder whereExpression(SearchParam searchParam) {
        BooleanBuilder builder = new BooleanBuilder();

        if(searchParam.getTotalPrice() != null){
            builder.and(
                    qOrder.totalPrice.eq(searchParam.getTotalPrice())
                            .or(qOrder.totalPrice.eq(searchParam.getTotalPrice()))
            );
        }

        if (searchParam.getOrderStatus() != null) {
            builder.and(qOrder.orderStatus.eq(searchParam.getOrderStatus()));
        }

        return builder;
    }

    private QResOrderDtoV1 getOrderProjection() {
        return new QResOrderDtoV1(
                qOrder.orderId,
                qOrder.totalPrice,
                qOrder.orderStatus,
                qOrder.store.name,
                qOrder.store.description,
                qOrder.createdAt
        );
    }

    private QResOrderDetailDtoV1 getOrderDetailProjection() {

        return new QResOrderDetailDtoV1(
                qOrder.orderId,
                qOrder.orderMessage,
                qOrder.totalPrice,
                qOrder.orderStatus,
                qOrder.store.name,
                qOrder.store.description,
                null, // 주문 메뉴
                qOrder.createdAt,
                qOrder.updatedAt
        );
    }
}
