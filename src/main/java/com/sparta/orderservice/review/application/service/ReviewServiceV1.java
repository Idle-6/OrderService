package com.sparta.orderservice.review.application.service;

import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.review.domain.entity.Review;
import com.sparta.orderservice.review.domain.repository.ReviewOrderRepository;
import com.sparta.orderservice.review.domain.repository.ReviewRepository;
import com.sparta.orderservice.review.presentation.advice.ReviewException;
import com.sparta.orderservice.review.presentation.dto.request.ReqReviewDtoV1;
import com.sparta.orderservice.review.presentation.dto.response.ResReviewDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceV1 {

    private final ReviewRepository reviewRepository;
    private final ReviewOrderRepository orderRepository;

    @Transactional
    public ResReviewDtoV1 createReview(Long userId, ReqReviewDtoV1 dto) {
        if (userId == null) throw ReviewException.unauthorized("로그인 정보가 없습니다");

        if (dto == null || dto.getOrderId() == null) throw ReviewException.invalidInput("orderId는 필수입니다");

        if (!StringUtils.hasText(dto.getContent())) throw ReviewException.invalidInput("content는 비어있을 수 없습니다");

        if (dto.getRating() < 1 || dto.getRating() > 5) throw ReviewException.invalidInput("rating은 1~5 범위여야 합니다");

        // 주문 조회 + 소유자 검증
        Order order = orderRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> ReviewException.notFound("주문을 찾을 수 없습니다. orderId=" + dto.getOrderId()));

        if (order.getUser() == null || !order.getUser().getUserId().equals(userId)) {
            throw ReviewException.accessDenied("본인 주문에만 리뷰를 작성할 수 있습니다");
        }

        // 중복 리뷰 방지(주문당 1개)
        if (reviewRepository.existsByOrder_OrderId(order.getOrderId())) throw ReviewException.alreadyReviewed("해당 주문에는 이미 리뷰가 존재합니다");

        try {
            Review review = Review.builder()
                    .order(order)
                    .content(dto.getContent())
                    .rating(dto.getRating())
                    .build();

            Review saved = reviewRepository.save(review);
            return toRes(saved);
        } catch (DataAccessException e) {
            throw ReviewException.dataAccess(e);
        }
    }
    private ResReviewDtoV1 toRes(Review r) {
        return ResReviewDtoV1.builder()
                .reviewId(r.getReviewId())
                .orderId(r.getOrder() != null ? r.getOrder().getOrderId() : null)
                .content(r.getContent())
                .rating(r.getRating())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ResReviewDtoV1> getReviewList(UUID storeId, int page, int size, String sort) {
        if (storeId == null) {
            throw ReviewException.invalidInput("storeId는 필수입니다.");
        }

        Pageable pageable = toPageable(page, size, sort);
        try {
            Page<Review> result = reviewRepository.findByStoreId(storeId, pageable);

            if (result.isEmpty()) {
                throw ReviewException.notFound("해당 가게의 리뷰가 없습니다. storeId=" + storeId);
            }

            return result.getContent().stream()
                    .map(this::toRes)
                    .toList();

        } catch (DataAccessException e) {
            throw ReviewException.dataAccess(e);
        }
    }

    private Pageable toPageable(int page, int size, String sortParam) {
        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), 100);

        //  필드는 createdAt으로 고정
        String field = "createdAt";
        Sort.Direction dir = Sort.Direction.DESC; // 최신순 기본

        if (sortParam != null && !sortParam.isBlank()) {
            String[] parts = sortParam.split(",", 2);

            // asc(오래된 순) 또는 desc(최신 순)만 허용
            if (parts.length > 1) {
                String direction = parts[1].trim();
                if (direction.equalsIgnoreCase("asc")) {
                    dir = Sort.Direction.ASC;
                } else if (direction.equalsIgnoreCase("desc")) {
                    dir = Sort.Direction.DESC;
                } else {
                    throw ReviewException.invalidSortField("정렬은 asc 또는 desc만 허용됩니다.");
                }
            }
        }

        return PageRequest.of(p, s, Sort.by(dir, field));
    }

}
