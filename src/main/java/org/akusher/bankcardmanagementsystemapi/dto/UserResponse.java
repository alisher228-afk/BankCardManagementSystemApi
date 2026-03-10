package org.akusher.bankcardmanagementsystemapi.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}
