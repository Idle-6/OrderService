package com.sparta.orderservice.review.domain.repository;

import com.sparta.orderservice.review.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByOrder_OrderId(UUID orderId);

    @Query(
            value = "SELECT r FROM Review r JOIN r.order o JOIN o.store s WHERE s.storeId = :storeId",
            countQuery = "SELECT COUNT(r) FROM Review r JOIN r.order o JOIN o.store s WHERE s.storeId = :storeId"
    )
    Page<Review> findByStoreId(@Param("storeId") UUID storeId, Pageable pageable);
}
