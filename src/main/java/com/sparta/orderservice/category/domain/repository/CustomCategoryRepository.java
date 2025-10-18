package com.sparta.orderservice.category.domain.repository;

import java.util.UUID;

public interface CustomCategoryRepository {

    boolean existsStore(UUID categoryId);
}
