package com.aryansinghdevelops.collegecommunitybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {
    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class AuthenticationRequest {
        private String email;
        private String password;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class AuthenticationResponse {
        private String token;
    }
}