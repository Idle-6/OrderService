package com.sparta.orderservice.category.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import com.sparta.orderservice.category.presentation.dto.response.ResCategoryDtoV1;
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

    public ResCategoryDtoV1 createCategory(ReqCategoryDtoV1 request) {

        // 카테고리 존재 여부
        existCategoryName(request.getName());

        // todo: 수정필요
        Category category = Category.ofNewCategory(request.getName(), null);

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
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        return convertResCategoryDto(category);
    }

    public ResCategoryDtoV1 updateCategory(UUID categoryId, ReqCategoryUpdateDtoV1 request) {

        existCategoryName(request.getName());

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // todo: 수정필요
        category.update(request.getName(), null);

        return convertResCategoryDto(category);
    }

    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // todo: 수정필요
        category.delete(null);
    }

    private ResCategoryDtoV1 convertResCategoryDto(Category category) {
        return new ResCategoryDtoV1(category.getCategoryId(), category.getName());
    }

    private void existCategoryName(String name) {
        if(categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 입니다.");
        }
    }

}
