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

import java.util.List;
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
        User user = userRepository.findById(request.getUserId().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Store store = storeRepository.findById(request.getStoreId().getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 주문 생성 (totalPrice 초기값 0으로)
        Order order = Order.ofNewOrder(
                request.getUserId(),
                request.getStoreId(),
                0,
                request.getOrderMessage(),
                request.getUserId()
        );

        int totalPrice = 0;

        // 주문메뉴 테이블에 수량 추가 및 총 금액 계산
        for (ReqOrderMenuDtoV1 menuDto : request.getOrderMenus()) {
            MenuEntity menu = menuRepository.findById(menuDto.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            OrderMenu orderMenu = OrderMenu.ofNew(order, menu, menuDto.getOrderMenuQty());
            order.addOrderMenu(orderMenu);
            totalPrice += orderMenu.getTotalPrice();
        }

        // 주문 테이블에 총 금액 업데이트
        order.updateTotalPrice(totalPrice);

        orderRepository.save(order);

        return new ResOrderDtoV1(
                order.getOrderId(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                store.getName(),
                store.getDescription(),
                order.getCreatedAt()
        );
    }

    // 주문 리스트 조회
    @Transactional(readOnly = true)
    public Page<ResOrderDtoV1> getOrders(SearchParam param, Pageable pageable) {
        return orderRepository.findOrderPage(param, pageable);
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public ResOrderDetailDtoV1 getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다."));

        // 주문 메뉴 DTO로 변환
        List<ReqOrderMenuDtoV1> orderMenus = order.getOrderMenus().stream()
                .map(om -> new ReqOrderMenuDtoV1(
                        om.getMenu().getId(),
                        om.getOrderMenuQty()
                ))
                .toList();

        return new ResOrderDetailDtoV1(
                order.getOrderId(),
                order.getOrderMessage(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getStore().getName(),
                order.getStore().getDescription(),
                orderMenus,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
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


