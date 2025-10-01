package com.sparta.orderservice.category.presentation.controller;

import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import com.sparta.orderservice.category.presentation.dto.response.ResCategoryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categorys")
public class CategoryControllerV1 {

    @PostMapping
    public ResponseEntity<ResCategoryDto> createCategory(@RequestBody @Valid ReqCategoryDtoV1 request) {

        ResCategoryDto response = new ResCategoryDto(UUID.randomUUID(), request.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResCategoryDto>> getCategoryList() {
        ResCategoryDto response = new ResCategoryDto(UUID.randomUUID(), "한식");

        List<ResCategoryDto> dtoList = List.of(response);

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDto> getCategory(@PathVariable UUID categoryId) {
        ResCategoryDto response = new ResCategoryDto(categoryId, "한식");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDto> updateCategory(@PathVariable UUID categoryId, @RequestBody @Valid ReqCategoryUpdateDtoV1 request) {
        ResCategoryDto response = new ResCategoryDto(categoryId, request.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {

        return ResponseEntity.ok().build();
    }

}
