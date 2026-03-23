package org.akusher.bankcardmanagementsystemapi.dto.user;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}
