package com.SmartShop.SmartShop.entities;


import com.SmartShop.SmartShop.entities.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerTier fidelite=CustomerTier.BASIC;

    // Statistiques automatiques pour le système de fidélité
    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrders = 0; // Nombre total de commandes CONFIRMED

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO; // Montant cumulé des commandes CONFIRMED

    private LocalDateTime firstOrderDate; // Date de la première commande

    private LocalDateTime lastOrderDate; // Date de la dernière commande

    @OneToMany(mappedBy = "client")
    private List<Order> orders;


}
