package com.sparta.orderservice.order.domain.entity;

import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_ordermenu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu {

    @Id
    @GeneratedValue
    private UUID orderMenuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuEntity menu;

    private int orderMenuQty;

    @Transient
    private int totalPrice;

    private OrderMenu(MenuEntity menu, int qty) {
        this.menu = menu;
        this.orderMenuQty = qty;
        this.totalPrice = menu.getPrice() * qty;
    }

    public static OrderMenu ofNew(Order order, MenuEntity menu, int qty) {
        OrderMenu orderMenu = new OrderMenu(menu, qty);
        orderMenu.setOrder(order);
        return orderMenu;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}