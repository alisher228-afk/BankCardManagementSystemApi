package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.CardResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.CardResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.akusher.bankcardmanagementsystemapi.entity.CardStatus;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.repository.AccountRepository;
import org.akusher.bankcardmanagementsystemapi.repository.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardResponseMapping cardResponseMapping;

    public CardService(CardRepository cardRepository, AccountRepository accountRepository, CardResponseMapping cardResponseMapping) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.cardResponseMapping = cardResponseMapping;
    }

    public Page<CardResponse> getUserCards(User user, Pageable pageable) {
        return cardRepository
                .findByAccountUser(user, pageable)
                .map(cardResponseMapping::toResponse);
    }

    public void blockCard(Long cardId , Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        card.setStatus(CardStatus.BLOCKED);
    }

    public void activateCard(Long cardId , Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        card.setStatus(CardStatus.ACTIVE);
    }
}
