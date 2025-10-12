package com.sparta.orderservice.store.presentation.controller;

import com.sparta.orderservice.store.application.service.StoreServiceV1;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
public class StoreControllerV1 {

    private final StoreServiceV1 storeService;

    @PostMapping
    public ResponseEntity<ResStoreDetailDtoV1> createStore(@RequestBody @Valid ReqStoreDtoV1 request) {
        ResStoreDetailDtoV1 response = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResStoreDtoV1>> getStorePage(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        SearchParam searchParam = new SearchParam(search, categoryId);
        Page<ResStoreDtoV1> response = storeService.getStorePage(searchParam, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> getStore(@PathVariable UUID storeId) {
        ResStoreDetailDtoV1 response = storeService.getStore(storeId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> updateStore(@PathVariable UUID storeId, @RequestBody @Valid ReqStoreUpdateDtoV1 request) {
        ResStoreDetailDtoV1 response = storeService.updateStore(storeId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.ok().build();
    }

}
