package com.sparta.orderservice.store.presentation.controller;

import com.sparta.orderservice.store.presentation.dto.request.ReqStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/stores")
public class StoreControllerV1 {

    @PostMapping
    public ResponseEntity<ResStoreDtoV1> createStore(@RequestBody @Valid ReqStoreDtoV1 request) {
        ResStoreDtoV1 response = new ResStoreDtoV1(
                UUID.randomUUID(),
                UUID.randomUUID(),
                request.getName(),
                request.getBizRegNo(),
                request.getContact(),
                request.getAddress(),
                request.getDescription(),
                100L,
                BigDecimal.valueOf(4.46),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResStoreDtoV1>> getStorePage(
            @RequestParam(required = false) UUID categoryId,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        ResStoreDtoV1 response = new ResStoreDtoV1(
                UUID.randomUUID(),
                categoryId != null? categoryId : UUID.randomUUID(),
                "홍길동 치킨",
                "123-45-67890",
                "02-747-1234",
                "서울특별시 강남구",
                "치킨집 입니다.",
                100L,
                BigDecimal.valueOf(4.46),
                LocalDateTime.now()
        );
        List<ResStoreDtoV1> stores = List.of(response);
        Page<ResStoreDtoV1> storeDtoV1Page = new PageImpl<>(stores, pageable, 0);

        return ResponseEntity.ok(storeDtoV1Page);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> getStore(@PathVariable UUID storeId) {
        ResStoreDetailDtoV1 response = new ResStoreDetailDtoV1(
                storeId,
                UUID.randomUUID(),
                "홍길동 치킨",
                "123-45-67890",
                "02-747-1234",
                "서울특별시 강남구",
                "치킨집 입니다.",
                true,
                100L,
                BigDecimal.valueOf(4.46),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<ResStoreDetailDtoV1> updateStore(@PathVariable UUID storeId, @RequestBody @Valid ReqStoreUpdateDtoV1 request) {
        ResStoreDetailDtoV1 response = new ResStoreDetailDtoV1(
                storeId,
                UUID.randomUUID(),
                request.getName() != null ? request.getName() : "홍길동 치킨",
                request.getBizRegNo() != null ? request.getBizRegNo() : "123-45-67890",
                request.getContact() != null ? request.getContact() : "02-747-1234",
                request.getAddress() != null ? request.getAddress() : "서울특별시 강남구",
                request.getDescription() != null ? request.getDescription() : "치킨집입니다.",
                request.isPublic(),
                100L,
                BigDecimal.valueOf(4.46),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId) {
        return ResponseEntity.ok().build();
    }

}
