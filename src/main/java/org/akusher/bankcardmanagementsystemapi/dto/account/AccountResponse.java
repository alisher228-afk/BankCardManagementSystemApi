package org.akusher.bankcardmanagementsystemapi.dto.account;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance
) {}