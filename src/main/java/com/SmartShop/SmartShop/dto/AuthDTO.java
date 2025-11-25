package com.SmartShop.SmartShop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String role;
        private Long clientId;
    }
}