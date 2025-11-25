package com.SmartShop.SmartShop.entities;


import com.SmartShop.SmartShop.entities.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.Mapping;

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

    @OneToMany(mappedBy = "client")
    private List<Order> orders;


}
