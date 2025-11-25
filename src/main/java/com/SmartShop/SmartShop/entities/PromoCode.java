package com.SmartShop.SmartShop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Format invalide. Exemple: PROMO-X9Y2")
    private String code;

    private int remisePercentage;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean utilisationUnique = true;

    private LocalDate dateExpiration;

}
