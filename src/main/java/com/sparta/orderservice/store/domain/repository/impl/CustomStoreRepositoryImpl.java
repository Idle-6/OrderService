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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    public Page<ResStoreDtoV1> findStorePage(SearchParam searchParam, Pageable pageable) {

        JPAQuery<ResStoreDtoV1> jpaQuery = query.select(getStoreProjection())
                .from(qStore)
                .join(qCategory).on(qStore.category.categoryId.eq(qCategory.categoryId))
                .where(whereExpression(searchParam),
                        qStore.isPublic.isTrue(),
                        qStore.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
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
                                    .where(whereExpression(searchParam), qStore.isPublic.isTrue());

        List<ResStoreDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
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
                .join(qUser).on(qStore.createdBy.eq(qUser))
                .where(qUser.userId.eq(userId))
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
