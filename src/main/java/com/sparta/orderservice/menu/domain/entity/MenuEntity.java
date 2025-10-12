package com.sparta.orderservice.menu.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "menu")
@Table(name = "menu")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
