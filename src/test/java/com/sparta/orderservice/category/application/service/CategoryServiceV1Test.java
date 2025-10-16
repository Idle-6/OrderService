package com.sparta.orderservice.category.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.category.presentation.advice.CategoryException;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryServiceV1Test {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceV1 categoryService;

    Category category;
    UUID categoryId;

    @BeforeEach
    void setUp() {
        category = Category.ofNewCategory("한식", 1L);
        categoryId = UUID.randomUUID();
        ReflectionTestUtils.setField(category, "categoryId", categoryId);
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory() {
        ReqCategoryDtoV1 request = new ReqCategoryDtoV1("중식");
        categoryService.createCategory(request, 1L);

        verify(categoryRepository, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 이름 중복")
    void createCategory_conflict() {
        ReqCategoryDtoV1 request = new ReqCategoryDtoV1("중식");
        when(categoryRepository.existsByName(request.getName())).thenReturn(true);

        assertThrows(CategoryException.class, () ->
                categoryService.createCategory(request, 1L)
        );

        verify(categoryRepository, Mockito.never()).save(Mockito.any(Category.class));
    }


    @Test
    @DisplayName("카테고리 리스트 조회")
    void getCategoryList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        categoryService.getCategoryList();

        verify(categoryRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("카테고리 조회")
    void getCategory() {
        when(categoryRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(category));

        categoryService.getCategory(categoryId);

        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any(UUID.class));
    }

    @Test
    @DisplayName("카테고리 조회 - 존재하지 않음")
    void getCategory_not_found() {
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () ->
                categoryService.getCategory(categoryId)
        );

        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("카테고리 수정")
    void updateCategory() {
        ReqCategoryUpdateDtoV1 request = new ReqCategoryUpdateDtoV1("양식");
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));

        categoryService.updateCategory(categoryId, request, 1L);

        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("카테고리 수정 - 존재하지 않음")
    void updateCategory_not_found() {
        ReqCategoryUpdateDtoV1 request = new ReqCategoryUpdateDtoV1("양식");
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () ->
            categoryService.updateCategory(categoryId, request, 1L)
        );

    }

    @Test
    @DisplayName("카테고리 수정 - 이름 중복")
    void updateCategory_conflict() {
        ReqCategoryUpdateDtoV1 request = new ReqCategoryUpdateDtoV1("양식");
        when(categoryRepository.existsByName(request.getName())).thenReturn(true);

        assertThrows(CategoryException.class, () ->
            categoryService.updateCategory(categoryId, request, 1L)
        );

    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() {
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));
        categoryService.deleteCategory(categoryId, 1L);

        assertAll(() -> {
            assertNotNull(category.getDeletedAt());
        });
    }
}