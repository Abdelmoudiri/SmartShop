package com.SmartShop.SmartShop.dto;

import com.SmartShop.SmartShop.entities.enums.CustomerTier;
import lombok.*;
import java.math.BigDecimal;

public class ClientDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;
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
    }
}
