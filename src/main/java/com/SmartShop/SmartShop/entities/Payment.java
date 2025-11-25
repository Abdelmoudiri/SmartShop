package com.SmartShop.SmartShop.entities;

import com.SmartShop.SmartShop.entities.enums.PaymentStatus;
import com.SmartShop.SmartShop.entities.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String numeroPaiement; // Ex: PAY-ORD1-01

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType typePaiement;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus statut = PaymentStatus.EN_ATTENTE;

    private LocalDate datePaiement; // Date déclarée

    private LocalDate dateEncaissement; // Date effective (Validation admin)

    private LocalDate dateEcheance; // Pour chèques/effets

    private String banque; // Optionnel (Pour chèque/virement)

    private String reference; // Numéro de chèque ou ref virement
}