package com.sparta.orderservice.menu.presentation.controller;

import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.menu.application.service.MenuServiceV1;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuUpdateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetByStoreIdDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetDtoV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menus")
public class MenuControllerV1 {

    private final MenuServiceV1 menuService;

    @PostMapping("")
    public ResponseEntity<ResMenuCreateDtoV1> createMenu(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam UUID storeId,
            @RequestBody @Valid ReqMenuCreateDtoV1 request
    ) {

        ResMenuCreateDtoV1 responseDto = menuService.createMenu(userDetails, storeId, request);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<Page<ResMenuGetByStoreIdDtoV1>> getMenuListByStoreId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam UUID storeId,
            @RequestParam("search") String search,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc
    ) {

        Page<ResMenuGetByStoreIdDtoV1> responseDto = menuService.getMenuList(userDetails, storeId, search, page, size, sortBy, isAsc);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<ResMenuGetDtoV1> getMenuById(@PathVariable("menuId") UUID menuId) {

        ResMenuGetDtoV1 responseDto = menuService.getMenu(menuId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{menuId}")
    public ResponseEntity<String> updateMenu(
            @PathVariable("menuId") UUID menuId,
            @RequestBody @Valid ReqMenuUpdateDtoV1 request
    ) {

        menuService.updateMenu(menuId, request);

        return ResponseEntity.ok("Menu updated");
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable("menuId") UUID menuId) {

        menuService.deleteMenu(menuId);

        return ResponseEntity.ok("Menu deleted");
    }
}
