package com.sparta.orderservice.manage.domain.repository;

import com.sparta.orderservice.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageOrderRepository extends JpaRepository<Order, UUID> {

}
