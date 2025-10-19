package com.sparta.orderservice.category.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;

import java.util.Optional;
import java.util.UUID;

public interface CustomCategoryRepository {

    boolean existsStore(UUID categoryId);

    Optional<Category> findCategoryById(UUID categoryId);
}
