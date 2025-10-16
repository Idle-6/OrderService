package com.sparta.orderservice.manage.domain.repository;

import com.sparta.orderservice.order.domain.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageOrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    List<Order> findByUser_UserId(Long userId);

    @EntityGraph(attributePaths = {"user", "store" /* , "orderMenus", "orderMenus.menu" */})
    Optional<Order> findByOrderId(UUID orderId);
}
