package com.sparta.orderservice.menu.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "menu")
@Table(name = "menu")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String name;

    private String description;

    private int price;

    @Column(name="is_public")
    private boolean isPublic;

//    @ManyToOne
//    @Column(name = "store_id")
//    private StoreEntity store;
}
