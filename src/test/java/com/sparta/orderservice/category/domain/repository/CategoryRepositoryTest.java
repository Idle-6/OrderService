package com.sparta.orderservice.category.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.global.infrastructure.querydsl.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager manager;

    Category category;

    @BeforeEach
    void setUp() {
        category = Category.ofNewCategory("한식", 1L);

        manager.persistAndFlush(category);
//        manager.clear();
    }

    @Test
    @DisplayName("카테고리 수정")
    void updateCategory_preUpdate() {

        category.update("양식", 2L);
        manager.flush();

        Category findCategory = manager.find(Category.class, category.getCategoryId());

        assertAll(() -> {
            assertEquals("양식", findCategory.getName());
            assertEquals(2L, findCategory.getUpdatedBy());
            assertNotNull(findCategory.getUpdatedAt());
        });
    }

    @Test
    @DisplayName("카테고리 존재")
    void existsByName_true() {
        boolean exist = categoryRepository.existsByName("한식");

        assertTrue(exist);
    }

    @Test
    @DisplayName("카테고리 존재하지 않음")
    void existsByName_false() {
        boolean exist = categoryRepository.existsByName("양식");

        assertFalse(exist);
    }
}