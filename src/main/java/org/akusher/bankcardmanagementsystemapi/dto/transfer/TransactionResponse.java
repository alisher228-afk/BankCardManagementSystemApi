package org.akusher.bankcardmanagementsystemapi.dto.transfer;

import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        String reference,
        String description,
        LocalDateTime createdAt
) {}