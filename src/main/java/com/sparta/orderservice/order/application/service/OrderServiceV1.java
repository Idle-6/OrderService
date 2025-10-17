package com.sparta.orderservice.order.application.service;

import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.menu.domain.repository.MenuRepository;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderMenu;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.order.domain.repository.OrderRepository;
import com.sparta.orderservice.order.presentation.advice.OrderErrorCode;
import com.sparta.orderservice.order.presentation.advice.OrderException;
import com.sparta.orderservice.order.presentation.advice.OrderExceptionLogUtils;
import com.sparta.orderservice.order.presentation.dto.SearchParam;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.request.ReqOrderMenuDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderCancelDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDetailDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderDtoV1;
import com.sparta.orderservice.order.presentation.dto.response.ResOrderUpdateDtoV1;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.store.presentation.advice.StoreErrorCode;
import com.sparta.orderservice.store.presentation.advice.StoreException;
import com.sparta.orderservice.store.presentation.advice.StoreExceptionLogUtils;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    // 주문 생성
    @Transactional
    public ResOrderDtoV1 createOrder(ReqOrderDtoV1 request) {
        Store store = storeRepository.findById(request.getStoreId().getStoreId())
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(request.getStoreId().getStoreId(), null)
                ));

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
        ResOrderDetailDtoV1 order = orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new OrderException(
                        OrderErrorCode.ORDER_NOT_FOUND,
                        OrderExceptionLogUtils.getNotFoundMessage(orderId, null)
                ));

        // 주문 메뉴 DTO로 변환
        List<ReqOrderMenuDtoV1> orderMenus = order.getOrderMenus().stream()
                .map(om -> new ReqOrderMenuDtoV1(
                        om.getMenuId(),
                        om.getOrderMenuQty()
                ))
                .toList();

        return new ResOrderDetailDtoV1(
                order.getOrderId(),
                order.getOrderMessage(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getStoreName(),
                order.getStoreDesc(),
                orderMenus,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    // 주문 상태 변경
    @Transactional
    public ResOrderUpdateDtoV1 updateOrderStatus(UUID orderId, OrderStatus orderStatus, User user) {
        Long userId = user.getUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(
                        OrderErrorCode.ORDER_NOT_FOUND,
                        OrderExceptionLogUtils.getNotFoundMessage(orderId, null)
                ));

        if (!hasPermission(user, order)) {
            throw new OrderException(
                    OrderErrorCode.ORDER_UPDATE_FORBIDDEN,
                    OrderExceptionLogUtils.getUpdateForbiddenMessage(orderId, userId)
            );
        }

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
    public ResOrderCancelDtoV1 cancelOrder(UUID orderId, User user) {
        Long userId = user.getUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(
                        OrderErrorCode.ORDER_NOT_FOUND,
                        OrderExceptionLogUtils.getNotFoundMessage(orderId, null)
                ));

        if (!hasPermission(user, order)) {
            throw new OrderException(
                    OrderErrorCode.ORDER_UPDATE_FORBIDDEN,
                    OrderExceptionLogUtils.getUpdateForbiddenMessage(orderId, userId)
            );
        }

        // 가게에서 주문 수락 전일 때만 취소 가능
        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new OrderException(
                    OrderErrorCode.ORDER_CANCEL_FORBIDDEN,
                    OrderExceptionLogUtils.getUpdateForbiddenMessage(orderId, userId)
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

    private boolean hasPermission(User user, Order order) {
        boolean isAdmin = Objects.equals(user.getRole().getAuthority(), UserRoleEnum.ADMIN.getAuthority());
        boolean isOwner = Objects.equals(order.getCreatedBy().getUserId(), user.getUserId());
        return isAdmin || isOwner;
    }

}


