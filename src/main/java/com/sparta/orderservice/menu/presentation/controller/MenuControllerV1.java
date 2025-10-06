package com.sparta.orderservice.menu.presentation.controller;

import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuUpdateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetByStoreIdDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetDtoV1;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/menus")
public class MenuControllerV1 {

    @PostMapping("")
    public ResponseEntity<ResMenuCreateDtoV1> createMenu(@RequestBody @Valid ReqMenuCreateDtoV1 request) {

        return ResponseEntity.ok(
                ResMenuCreateDtoV1.builder()
                    .id(UUID.randomUUID())
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .isPublic(request.isPublic())
                    .build()
        );
    }

    @GetMapping("/{storeId}/menu")
    public ResponseEntity<Page<ResMenuGetByStoreIdDtoV1>> getMenuListByStoreId(
            @PathVariable UUID storeId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc
    ) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<ResMenuGetByStoreIdDtoV1> menuList = Arrays.asList(
                new ResMenuGetByStoreIdDtoV1(UUID.randomUUID(), "자장면", "", 9000, true),
                new ResMenuGetByStoreIdDtoV1(UUID.randomUUID(), "짬뽕", "신라면 정도 매움", 10000, true),
                new ResMenuGetByStoreIdDtoV1(UUID.randomUUID(), "탕수육", "쫄깃 바삭한 튀김", 15000, true),
                new ResMenuGetByStoreIdDtoV1(UUID.randomUUID(), "우육면", "", 9000, false),
                new ResMenuGetByStoreIdDtoV1(UUID.randomUUID(), "팔보채", "", 15000, true)
        );

        Page<ResMenuGetByStoreIdDtoV1> productList = new PageImpl<>(menuList, pageable, menuList.size());

        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<ResMenuGetDtoV1> getMenuById(@PathVariable("menuId") UUID menuId) {

        return ResponseEntity.ok(
            ResMenuGetDtoV1.builder()
                .id(menuId)
                .name("짬뽕")
                .description("신라면 정도 매움")
                .price(10000)
                .isPublic(true)
                .build()
        );
    }

    @PatchMapping("/{menuId}")
    public ResponseEntity<String> updateMenu(
            @PathVariable("menuId") UUID menuId,
            @RequestBody @Valid ReqMenuUpdateDtoV1 request
    ) {

        return new ResponseEntity<>("Menu updated", HttpStatus.OK);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable("menuId") UUID menuId) {

        return new ResponseEntity<>("Menu deleted", HttpStatus.OK);
    }
}
