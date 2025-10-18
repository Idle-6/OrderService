package com.sparta.orderservice.order.domain.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.menu.domain.entity.QMenuEntity;
import com.sparta.orderservice.order.domain.entity.QOrder;
import com.sparta.orderservice.order.domain.entity.QOrderMenu;
import com.sparta.orderservice.order.domain.repository.CustomOrderRepository;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.QResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.QResOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.store.domain.entity.QStore;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.presentation.advice.UserErrorCode;
import com.sparta.orderservice.user.presentation.advice.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.*;

@RequiredArgsConstructor
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    private final JPAQueryFactory query;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("totalPrice", "createdAt");

    QOrder qOrder = QOrder.order;

    @Override
    public Page<ResOrderDtoV1> findOrderPage(SearchParam searchParam, Pageable pageable, User user) {
        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<ResOrderDtoV1> jpaQuery = query
                .select(getOrderProjection())
                .from(qOrder)
                .join(qOrder.store)
                .where(whereExpression(searchParam, user))
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        // 정렬 처리
        if (adjustedPageable.getSort().isSorted()) {
            for (Sort.Order order : adjustedPageable.getSort()) {
                if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) continue;

                if ("createdAt".equals(order.getProperty())) {
                    jpaQuery.orderBy(order.isAscending() ? qOrder.createdAt.asc() : qOrder.createdAt.desc());
                } else if ("totalPrice".equals(order.getProperty())) {
                    jpaQuery.orderBy(order.isAscending() ? qOrder.totalPrice.asc() : qOrder.totalPrice.desc());
                }
            }
        } else {
            jpaQuery.orderBy(qOrder.createdAt.desc());
        }

        // 총 개수
        JPAQuery<Long> countQuery = query
                .select(qOrder.count())
                .from(qOrder)
                .join(qOrder.store)
                .where(whereExpression(searchParam, user));

        List<ResOrderDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, adjustedPageable, countQuery::fetchOne);
    }

    @Override
    public Optional<ResOrderDetailDtoV1> findOrderDetailById(UUID orderId) {

        QOrder qOrder = QOrder.order;
        QOrderMenu qOrderMenu = QOrderMenu.orderMenu;
        QStore qStore = QStore.store;
        QMenuEntity qMenu = QMenuEntity.menuEntity;

        // 주문 + 매장 + 주문메뉴 조인
        List<Tuple> results = query
                .select(
                        qOrder.orderId,
                        qOrder.orderMessage,
                        qOrder.totalPrice,
                        qOrder.orderStatus,
                        qStore.name,
                        qStore.description,
                        qMenu.id,
                        qOrderMenu.orderMenuQty,
                        qOrder.createdAt,
                        qOrder.updatedAt
                )
                .from(qOrder)
                .join(qOrder.store, qStore)
                .leftJoin(qOrder.orderMenus, qOrderMenu)
                .leftJoin(qOrderMenu.menu, qMenu)
                .where(qOrder.orderId.eq(orderId))
                .fetch();

        if (results.isEmpty()) return Optional.empty();

        // 첫 번째 row 기준으로 주문 기본 정보 구성
        Tuple first = results.get(0);
        List<ReqOrderMenuDtoV1> orderMenus = results.stream()
                .filter(tuple -> tuple.get(qMenu.id) != null)
                .map(tuple -> new ReqOrderMenuDtoV1(
                        tuple.get(qMenu.id),
                        tuple.get(qOrderMenu.orderMenuQty)
                ))
                .toList();

        // 주문 상세 DTO로 조립
        ResOrderDetailDtoV1 dto = new ResOrderDetailDtoV1(
                first.get(qOrder.orderId),
                first.get(qOrder.orderMessage),
                first.get(qOrder.totalPrice),
                first.get(qOrder.orderStatus),
                first.get(qStore.name),
                first.get(qStore.description),
                orderMenus,
                first.get(qOrder.createdAt),
                first.get(qOrder.updatedAt)
        );

        return Optional.of(dto);
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

    private BooleanBuilder whereExpression(SearchParam searchParam, User user) {
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 필터
        if(searchParam.getTotalPrice() != null){
            builder.and(
                    qOrder.totalPrice.eq(searchParam.getTotalPrice())
                            .or(qOrder.totalPrice.eq(searchParam.getTotalPrice()))
            );
        }

        if (searchParam.getOrderStatus() != null) {
            builder.and(qOrder.orderStatus.eq(searchParam.getOrderStatus()));
        }

        // 권한 필터
        if (user.getRole() == UserRoleEnum.OWNER) {
            // 가게 주인은 본인이 소유한 가게 주문만 조회
            builder.and(qOrder.store.createdBy.userId.eq(user.getUserId()));
        } else if (user.getRole() == UserRoleEnum.USER) {
            // 일반 사용자는 본인이 주문한 것만 조회
            builder.and(qOrder.user.userId.eq(user.getUserId()));
        } else if (user.getRole() == UserRoleEnum.ADMIN) {
            // Admin은 모든 주문 조회
        } else {
            throw new UserException(UserErrorCode.USER_UNKNOWN_AUTHORITY);
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
                Expressions.nullExpression(), // 주문 메뉴
                qOrder.createdAt,
                qOrder.updatedAt
        );
    }
}
