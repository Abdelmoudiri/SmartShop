package com.SmartShop.SmartShop.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;



}
