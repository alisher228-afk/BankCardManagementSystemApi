package org.akusher.bankcardmanagementsystemapi.dto;

public record UpdateUserRequest(
        String username,
        String email
) {}