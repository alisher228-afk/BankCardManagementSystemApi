package org.akusher.bankcardmanagementsystemapi.dto.mapping;

import org.akusher.bankcardmanagementsystemapi.dto.CardResponse;
import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.springframework.stereotype.Component;

@Component
public  class CardResponseMapping {
    public CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getPanLast4(),
                card.getStatus(),
                card.getExpiryDate()
        );
    }
}