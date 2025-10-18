package com.sparta.orderservice.category.presentation.controller;

import com.sparta.orderservice.category.application.service.CategoryServiceV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import com.sparta.orderservice.category.presentation.dto.response.ResCategoryDtoV1;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categorys")
@RequiredArgsConstructor
public class CategoryControllerV1 {

    private final CategoryServiceV1 categoryService;

    @PostMapping
    public ResponseEntity<ResCategoryDtoV1> createCategory(@RequestBody @Valid ReqCategoryDtoV1 request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResCategoryDtoV1 response = categoryService.createCategory(request, userDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResCategoryDtoV1>> getCategoryList() {
        List<ResCategoryDtoV1> dtoList = categoryService.getCategoryList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDtoV1> getCategory(@PathVariable UUID categoryId) {
        ResCategoryDtoV1 response = categoryService.getCategory(categoryId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDtoV1> updateCategory(@PathVariable UUID categoryId, @RequestBody @Valid ReqCategoryUpdateDtoV1 request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResCategoryDtoV1 response = categoryService.updateCategory(categoryId, request, userDetails.getUser().getUserId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        categoryService.deleteCategory(categoryId, userDetails.getUser().getUserId());

        return ResponseEntity.ok().build();
    }

}
