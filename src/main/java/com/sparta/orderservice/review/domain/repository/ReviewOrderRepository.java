package com.sparta.orderservice.review.domain.repository;

import com.sparta.orderservice.order.domain.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewOrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"user"})
    Optional<Order> findByOrderId(UUID orderId);
}
