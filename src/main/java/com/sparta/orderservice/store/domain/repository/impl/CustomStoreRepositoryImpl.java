package com.sparta.orderservice.store.domain.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.category.domain.entity.QCategory;
import com.sparta.orderservice.store.domain.entity.QStore;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.CustomStoreRepository;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.response.QResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.QResStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import com.sparta.orderservice.user.domain.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.*;

@RequiredArgsConstructor
public class CustomStoreRepositoryImpl implements CustomStoreRepository {

    private final JPAQueryFactory query;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "name", "address", "createdAt", "reviewCount", "averageRating"
    );

    QStore qStore = QStore.store;
    QCategory qCategory = QCategory.category;
    QUser qUser = QUser.user;

    @Override
    public Page<ResStoreDtoV1> findStorePage(SearchParam searchParam, Pageable pageable, boolean isAdmin) {

        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<ResStoreDtoV1> jpaQuery = query.select(getStoreProjection())
                .from(qStore)
                .join(qCategory).on(qStore.category.categoryId.eq(qCategory.categoryId))
                .where(whereExpression(searchParam), permissionCondition(isAdmin))
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        if (adjustedPageable.getSort().isSorted()) {
            for (Sort.Order order : adjustedPageable.getSort()) {
                if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                    continue;
                }
                PathBuilder<Store> entityPath = new PathBuilder<>(Store.class, "store");
                jpaQuery.orderBy(new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ));
            }
        } else {
            jpaQuery.orderBy(qStore.createdAt.desc());
        }

        JPAQuery<Long> count = query
                                    .select(qStore.count())
                                    .from(qStore)
                                    .where(whereExpression(searchParam), permissionCondition(isAdmin));

        List<ResStoreDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, adjustedPageable, count::fetchOne);
    }

    @Override
    public Optional<ResStoreDetailDtoV1> findStoreDetailById(UUID storeId) {

        ResStoreDetailDtoV1 response = query.select(getStoreDetailProjection())
                .from(qStore)
                .join(qCategory).on(qStore.category.categoryId.eq(qCategory.categoryId))
                .where(qStore.storeId.eq(storeId), qStore.isPublic.isTrue())
                .fetchOne();

        return Optional.ofNullable(response);
    }

    @Override
    public Optional<ResStoreDetailDtoV1> findStoreDetailByUserId(Long userId) {

        ResStoreDetailDtoV1 response = query.select(getStoreDetailProjection())
                .from(qStore)
                .join(qCategory).on(qStore.category.categoryId.eq(qCategory.categoryId))
                .where(qStore.createdBy.userId.eq(userId))
                .fetchOne();
        return Optional.ofNullable(response);
    }

    @Override
    public boolean existsStoreByUserId(Long userId) {

        Long exist = query.select(qStore.count())
                .from(qStore)
                .where(qStore.createdBy.userId.eq(userId))
                .fetchOne();
        return exist != null && exist  > 0;
    }

    private BooleanBuilder whereExpression(SearchParam searchParam) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(searchParam.getTerm() != null){
            booleanBuilder.and(
                    qStore.name.contains(searchParam.getTerm())
                            .or(qStore.address.contains(searchParam.getTerm()))
                            .or(qStore.description.contains(searchParam.getTerm()))
            );
        }

        if(searchParam.getCategoryId() != null) {
            booleanBuilder.and(qStore.category.categoryId.eq(searchParam.getCategoryId()));
        }

        return booleanBuilder;
    }

    private BooleanBuilder permissionCondition(boolean isAdmin) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(!isAdmin) {
            booleanBuilder.and(qStore.isPublic.isTrue())
                    .and(qStore.deletedAt.isNull());
        }

        return booleanBuilder;
    }

    private QResStoreDtoV1 getStoreProjection() {
        return new QResStoreDtoV1(
                qStore.storeId,
                qCategory.name,
                qStore.name,
                qStore.contact,
                qStore.address,
                qStore.description,
                qStore.reviewCount,
                qStore.averageRating
        );
    }

    private QResStoreDetailDtoV1 getStoreDetailProjection() {
        return new QResStoreDetailDtoV1(
                qStore.storeId,
                qCategory.name,
                qStore.name,
                qStore.bizRegNo,
                qStore.contact,
                qStore.address,
                qStore.description,
                qStore.isPublic,
                qStore.reviewCount,
                qStore.averageRating,
                qStore.createdAt,
                qStore.updatedAt
        );
    }
}
