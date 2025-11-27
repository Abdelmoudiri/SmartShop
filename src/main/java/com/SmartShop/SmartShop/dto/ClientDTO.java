package com.SmartShop.SmartShop.dto;

import com.SmartShop.SmartShop.entities.enums.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClientDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        private String email;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;

        @Email(message = "Format d'email invalide")
        private String email;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nom;
        private String email;
        private CustomerTier fidelite;
        private Integer totalOrders;
        private BigDecimal totalSpent;
        private LocalDateTime firstOrderDate;
        private LocalDateTime lastOrderDate;
    }
}
