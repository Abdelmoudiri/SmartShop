package com.SmartShop.SmartShop.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

public class ProductDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Le nom du produit est obligatoire")
        private String nom;

        @NotNull
        @Positive(message = "Le prix doit être positif")
        private BigDecimal prixUnitaire;

        @NotNull
        @Min(value = 0, message = "Le stock ne peut pas être négatif")
        private Integer stock;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nom;
        private BigDecimal prixUnitaire;
        private Integer stock;
    }
}