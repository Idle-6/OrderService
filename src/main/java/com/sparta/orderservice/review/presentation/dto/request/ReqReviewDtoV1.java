package com.sparta.orderservice.review.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqReviewDtoV1 {

    private UUID orderId;
    private String content;
    private int rating;
}
