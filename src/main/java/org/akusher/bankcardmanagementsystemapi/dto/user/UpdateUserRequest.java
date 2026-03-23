package org.akusher.bankcardmanagementsystemapi.dto.user;

public record UpdateUserRequest(
        String username,
        String email
) {}