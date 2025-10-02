package com.sparta.orderservice.category.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager manager;

    Category category;

    @BeforeEach
    void setUp() {
        category = Category.ofNewCategory("한식", 1L);

        manager.persist(category);
        manager.flush();
        manager.clear();
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