package com.sparta.orderservice.category.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.category.presentation.advice.CategoryErrorCode;
import com.sparta.orderservice.category.presentation.advice.CategoryException;
import com.sparta.orderservice.category.presentation.advice.CategoryExceptionLogUtils;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import com.sparta.orderservice.category.presentation.dto.response.ResCategoryDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceV1 {

    private final CategoryRepository categoryRepository;

    public ResCategoryDtoV1 createCategory(ReqCategoryDtoV1 request, Long userId) {
        // 카테고리 존재 여부
        checkCategoryNameDuplication(request.getName());

        Category category = Category.ofNewCategory(request.getName(), userId);

        categoryRepository.save(category);

        return convertResCategoryDto(category);
    }

    @Transactional(readOnly = true)
    public List<ResCategoryDtoV1> getCategoryList() {

        List<Category> categoryList = categoryRepository.findAll();
        List<ResCategoryDtoV1> resCategoryDtoV1List = new ArrayList<>();

        for(Category category : categoryList) {
            resCategoryDtoV1List.add(convertResCategoryDto(category));
        }

        return resCategoryDtoV1List;
    }

    @Transactional(readOnly = true)
    public ResCategoryDtoV1 getCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(
                        CategoryErrorCode.CATEGORY_NOT_FOUND,
                        CategoryExceptionLogUtils.getNotFoundMessage(categoryId, null)
                ));

        return convertResCategoryDto(category);
    }

    public ResCategoryDtoV1 updateCategory(UUID categoryId, ReqCategoryUpdateDtoV1 request, Long userId) {
        checkCategoryNameDuplication(request.getName());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(
                        CategoryErrorCode.CATEGORY_NOT_FOUND,
                        CategoryExceptionLogUtils.getNotFoundMessage(categoryId, null)
                ));

        category.update(request.getName(), userId);

        return convertResCategoryDto(category);
    }

    public void deleteCategory(UUID categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(
                        CategoryErrorCode.CATEGORY_NOT_FOUND,
                        CategoryExceptionLogUtils.getNotFoundMessage(categoryId, null)
                ));

        category.delete(userId);
    }

    private ResCategoryDtoV1 convertResCategoryDto(Category category) {
        return new ResCategoryDtoV1(category.getCategoryId(), category.getName());
    }

    private void checkCategoryNameDuplication(String name) {
        if(categoryRepository.existsByName(name)) {
            throw new CategoryException(
                    CategoryErrorCode.CATEGORY_CONFLICT,
                    CategoryExceptionLogUtils.getConflictMessage(name, null)
            );
        }
    }

}
