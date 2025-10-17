package com.sparta.orderservice.order.domain.repository;

import com.sparta.orderservice.order.domain.entity.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {
}