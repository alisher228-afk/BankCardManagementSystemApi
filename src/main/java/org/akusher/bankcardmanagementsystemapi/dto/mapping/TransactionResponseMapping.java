package org.akusher.bankcardmanagementsystemapi.dto.mapping;

import org.akusher.bankcardmanagementsystemapi.dto.TransactionResponse;
import org.akusher.bankcardmanagementsystemapi.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionResponseMapping {

    public TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getFromAccount() != null ? tx.getFromAccount().getId() : null,
                tx.getToAccount() != null ? tx.getToAccount().getId() : null,
                tx.getAmount(),
                tx.getCurrency(),
                tx.getStatus(),
                tx.getReference(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}