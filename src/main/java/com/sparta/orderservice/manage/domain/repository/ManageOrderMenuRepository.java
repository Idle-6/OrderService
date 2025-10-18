package com.sparta.orderservice.manage.domain.repository;

import com.sparta.orderservice.order.domain.entity.OrderMenu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManageOrderMenuRepository extends JpaRepository<OrderMenu, UUID> {

    // 주문 상세에서 메뉴까지 한 번에 로딩
    @EntityGraph(attributePaths = {"menu"})
    List<OrderMenu> findByOrder_OrderId(UUID orderId);
}
