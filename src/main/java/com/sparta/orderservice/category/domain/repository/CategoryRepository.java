package com.sparta.orderservice.category.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, CustomCategoryRepository {

    boolean existsByName(String name);

    List<Category> findAllByDeletedAtIsNull();
}
