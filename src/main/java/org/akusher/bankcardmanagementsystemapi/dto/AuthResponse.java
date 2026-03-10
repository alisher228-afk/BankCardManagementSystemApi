package org.akusher.bankcardmanagementsystemapi.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}