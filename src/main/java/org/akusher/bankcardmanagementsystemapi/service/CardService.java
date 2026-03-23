package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.card.CardResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.CardResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.Account;
import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.CardStatus;
import org.akusher.bankcardmanagementsystemapi.repository.AccountRepository;
import org.akusher.bankcardmanagementsystemapi.repository.CardRepository;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.akusher.bankcardmanagementsystemapi.exception.AccessDeniedException;
import org.akusher.bankcardmanagementsystemapi.exception.AccountNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Base64;

@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardResponseMapping cardResponseMapping;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, AccountRepository accountRepository, CardResponseMapping cardResponseMapping, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.cardResponseMapping = cardResponseMapping;
        this.userRepository = userRepository;
    }

    public Page<CardResponse> getUserCards(User user, Pageable pageable) {
        return cardRepository
                .findByAccountUser(user, pageable)
                .map(cardResponseMapping::mapToResponse);
    }

    public void blockCard(Long cardId , Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AccountNotFoundException(cardId));
        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized");
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

    public CardResponse create(String username, Long accountId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        String cardNumber = generateCardNumber();

        Card card = new Card();
        card.setAccount(account);
        card.setPanLast4(cardNumber.substring(cardNumber.length() - 4));
        card.setEncryptedPan(encrypt(cardNumber));
        card.setExpiryDate(YearMonth.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);

        Card saved = cardRepository.save(card);

        return new CardResponse(
                saved.getId(),
                saved.getPanLast4(),
                saved.getStatus(),
                saved.getExpiryDate()
        );
    }

    public void delete(String username, Long cardId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getAccount().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cardRepository.delete(card);
    }


    private String generateCardNumber() {
        return String.valueOf(
                4000000000000000L +
                        (long)(Math.random() * 100000000000000L)
        );
    }
    private String encrypt(String pan) {
        return Base64.getEncoder().encodeToString(pan.getBytes());
    }
}
