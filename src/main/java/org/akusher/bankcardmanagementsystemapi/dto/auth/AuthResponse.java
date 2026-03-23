package org.akusher.bankcardmanagementsystemapi.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}