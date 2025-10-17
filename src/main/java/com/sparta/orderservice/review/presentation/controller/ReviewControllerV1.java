package com.sparta.orderservice.review.presentation.controller;

import com.sparta.orderservice.review.presentation.dto.response.ResReviewDetailDtoV1;
import com.sparta.orderservice.review.presentation.dto.response.ResReviewDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/Reviews")
public class ReviewControllerV1 {

    @PostMapping
    public ResponseEntity<ResReviewDtoV1> createReview(){

        return null;
    }

    @GetMapping
    public ResponseEntity<List<ResReviewDtoV1>> getReviewList() {
        return null;
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ResReviewDetailDtoV1> getReview(@PathVariable UUID reviewId) {
        return null;
    }
}
