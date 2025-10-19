package com.sparta.orderservice.category.domain.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.entity.QCategory;
import com.sparta.orderservice.category.domain.repository.CustomCategoryRepository;
import com.sparta.orderservice.store.domain.entity.QStore;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

    private final JPAQueryFactory query;

    QCategory qCategory = QCategory.category;
    QStore qStore = QStore.store;

    @Override
    public boolean existsStore(UUID categoryId) {
        Long count = query.select(qStore.count())
                .from(qStore)
                .where(qStore.category.categoryId.eq(categoryId))
                .fetchOne();

        return count != null && count > 0;
    }

    public Optional<Category> findCategoryById(UUID categoryId) {
        Category category = query.select(qCategory)
                .from(qCategory)
                .where(qCategory.categoryId.eq(categoryId), qCategory.deletedAt.isNull())
                .fetchOne();

        return Optional.ofNullable(category);
    }
}
