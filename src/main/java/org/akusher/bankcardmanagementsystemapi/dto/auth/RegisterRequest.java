package org.akusher.bankcardmanagementsystemapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 4, max = 20)
        String username,
        @Email
        String email,
        @NotBlank
        @Size(min = 6)
        String password
) {}
