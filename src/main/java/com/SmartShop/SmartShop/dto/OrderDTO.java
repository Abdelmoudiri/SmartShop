package com.SmartShop.SmartShop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotNull(message = "L'ID du client est obligatoire")
        private Long clientId;
        @NotEmpty(message = "La commande doit contenir au moins un article")
        private List<OrderItemRequest> items;

        private String codePromo;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull
        private Long productId;

        @Positive(message = "La quantité doit être supérieure à 0")
        private Integer quantite;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDateTime date;
        private String status;
        private String clientNom;

        private BigDecimal sousTotal;
        private int remiseFidelitePercentage;
        private String codePromoApplique;
        private BigDecimal montantRemise;
        private BigDecimal tva;
        private BigDecimal totalTTC;
        private BigDecimal montantRestant;

        private List<OrderItemResponse> items;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemResponse {
        private Long productId;
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixUnitaireSnapshot;
        private BigDecimal totalLigne;
    }
}