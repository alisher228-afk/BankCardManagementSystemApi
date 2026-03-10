package org.akusher.bankcardmanagementsystemapi.dto;

import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.CardStatus;

import java.time.YearMonth;

public record CardResponse(
        Long id,
        String last4,
        CardStatus status,
        YearMonth expiryDate
) {}