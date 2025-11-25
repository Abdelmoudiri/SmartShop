package com.SmartShop.SmartShop.dto;

import com.SmartShop.SmartShop.entities.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotNull
        private Long orderId;

        @NotNull
        @Positive
        private BigDecimal montant;

        @NotNull
        private PaymentType typePaiement;

        private String reference;
        private String banque;
        private LocalDate dateEcheance;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String numeroPaiement;
        private BigDecimal montant;
        private String typePaiement;
        private String statut;
        private LocalDate datePaiement;
    }
}