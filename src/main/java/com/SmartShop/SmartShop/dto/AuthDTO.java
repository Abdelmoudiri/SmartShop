package com.SmartShop.SmartShop.dto;

import com.SmartShop.SmartShop.entities.enums.UserRole;
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

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static  class RegisterRequest{
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        private UserRole role;


    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String role;
        private Long clientId;
    }
}