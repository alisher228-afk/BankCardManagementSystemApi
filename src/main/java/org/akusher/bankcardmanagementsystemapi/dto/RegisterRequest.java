package org.akusher.bankcardmanagementsystemapi.dto;

import org.akusher.bankcardmanagementsystemapi.entity.Role;

public record RegisterRequest(
        String username,
        String password,
        String email
) {
}
