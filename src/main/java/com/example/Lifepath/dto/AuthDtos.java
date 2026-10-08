package com.example.Lifepath.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public record Register(@NotBlank @Size(max = 100) String name, @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String password) {
    }

    public record Login(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record UserView(Long id, String name, String email, String role) {
    }

    public record AuthResponse(String token, UserView user) {
    }
}
