package com.sparta.orderservice.order.application.service;

import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.menu.domain.repository.MenuRepository;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderMenu;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderCancelDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderUpdateDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    // 주문 생성
    @Transactional
    public ResOrderDtoV1 createOrder(ReqOrderDtoV1 request) {
        // todo : 회원, 가게 조회

        Order order = Order.ofNewOrder(
                request.getUserId(),
                request.getStoreId(),
                request.getTotalPrice(),
                request.getOrderMessage(),
                null );

        orderRepository.save(order);

        return new ResOrderDtoV1(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getStore().getStoreId(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getCreatedAt() );
    }

    // 주문 리스트 조회
    @Transactional(readOnly = true)
    public Page<ResOrderDtoV1> getOrders(SearchParam param, Pageable pageable) {
        return orderRepository.findOrderPage(param, pageable);
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public ResOrderDetailDtoV1 getOrderDetail(UUID orderId) {
        return orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));
    }

    // 주문 상태 변경
    @Transactional
    public ResOrderUpdateDtoV1 updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        order.updateOrderStatus(orderStatus, null);
        orderRepository.save(order);

        return new ResOrderUpdateDtoV1(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getUpdatedAt()
        );
    }

    // 주문 취소
    @Transactional
    public ResOrderCancelDtoV1 cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        // 가게에서 주문 수락 전일 때만 취소 가능
        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    String.format("주문 수락 이후에는 취소할 수 없습니다. 현재 상태: %s", order.getOrderStatus())
            );
        }

        order.cancelOrder(null);
        orderRepository.save(order);

        // TODO: 결제 취소 로직 추가

        return new ResOrderCancelDtoV1(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getUpdatedAt()
        );
    }

}


