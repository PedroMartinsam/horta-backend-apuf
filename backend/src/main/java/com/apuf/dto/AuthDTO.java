package com.apuf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String nome;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        private String password;

        private String telefone;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private UserInfo user;

        @Data
        public static class UserInfo {
            private Long id;
            private String nome;
            private String email;
            private String role;
        }
    }
}
