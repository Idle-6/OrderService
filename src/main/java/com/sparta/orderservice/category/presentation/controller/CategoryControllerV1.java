package com.sparta.orderservice.category.presentation.controller;

import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryDtoV1;
import com.sparta.orderservice.category.presentation.dto.request.ReqCategoryUpdateDtoV1;
import com.sparta.orderservice.category.presentation.dto.response.ResCategoryDtoV1;
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
    public ResponseEntity<ResCategoryDtoV1> createCategory(@RequestBody @Valid ReqCategoryDtoV1 request) {

        ResCategoryDtoV1 response = new ResCategoryDtoV1(UUID.randomUUID(), request.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResCategoryDtoV1>> getCategoryList() {
        ResCategoryDtoV1 response = new ResCategoryDtoV1(UUID.randomUUID(), "한식");

        List<ResCategoryDtoV1> dtoList = List.of(response);

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDtoV1> getCategory(@PathVariable UUID categoryId) {
        ResCategoryDtoV1 response = new ResCategoryDtoV1(categoryId, "한식");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ResCategoryDtoV1> updateCategory(@PathVariable UUID categoryId, @RequestBody @Valid ReqCategoryUpdateDtoV1 request) {
        ResCategoryDtoV1 response = new ResCategoryDtoV1(categoryId, request.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {

        return ResponseEntity.ok().build();
    }

}
