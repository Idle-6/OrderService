package com.sparta.orderservice.menu.presentation.controller;

import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuCreateDtoV1;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/menu")
public class MenuControllerV1 {

    @PostMapping("")
    public ResMenuCreateDtoV1 createMenu(@RequestBody @Valid ReqMenuCreateDtoV1 request) {

        return ResMenuCreateDtoV1.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .isPublic(request.isPublic())
                .build();
    }

//    @GetMapping("{storeId}/menu")
//    public List<ResMenuGetByStoreIdDtoV1> getMenuListByStoreId(
//            @PathVariable UUID storeId,
//            @RequestParam("page") int page,
//            @RequestParam("size") int size,
//            @RequestParam("sortBy") String sortBy,
//            @RequestParam("isAsc") boolean isAsc
//    ) {
//        return "Menu retrieved";
//    }
//
//    @GetMapping("{menuId}")
//    public ResMenuGetDtoV1 getMenuById(@PathVariable("menuId") UUID menuId) {
//        return "Menu retrieved";
//    }
//
//    @PatchMapping("{menuId}")
//    public ResponseEntity<String> updateMenu(
//            @PathVariable("menuId") UUID menuId,
//            @RequestBody @Valid ReqMenuUpdateDtoV1 request
//    ) {
//        return new ResponseEntity<>("Menu updated", HttpStatus.OK);
//    }
//
//    @DeleteMapping("{menuId}")
//    public ResponseEntity<String> deleteMenu(@PathVariable("menuId") UUID menuId) {
//        return new ResponseEntity<>("Menu deleted", HttpStatus.OK);
//    }
}
