package com.sparta.orderservice.review.presentation.controller;

import com.sparta.orderservice.review.application.service.ReviewServiceV1;
import com.sparta.orderservice.review.presentation.dto.request.ReqReviewDtoV1;
import com.sparta.orderservice.review.presentation.dto.response.ResReviewDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewControllerV1 {

    private final ReviewServiceV1 reviewService;

    @PostMapping
    public ResponseEntity<ResReviewDtoV1> createReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReqReviewDtoV1 reviewDto
    ){
        ResReviewDtoV1 resReviewDto =reviewService.createReview(userId, reviewDto);
        return ResponseEntity.ok(resReviewDto);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<ResReviewDtoV1>> getReviewList(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        List<ResReviewDtoV1> list = reviewService.getReviewList(storeId, page, size, sort);
        return ResponseEntity.ok(list);
    }

}
