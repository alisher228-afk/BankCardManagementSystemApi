package org.akusher.bankcardmanagementsystemapi.controller;

import org.akusher.bankcardmanagementsystemapi.dto.card.CardResponse;
import org.akusher.bankcardmanagementsystemapi.dto.card.CreateCardRequest;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
    @GetMapping
    public Page<CardResponse> getUserCards(
    @AuthenticationPrincipal User user
            , Pageable pageable) {
        return cardService.getUserCards(user, pageable);
    }

    @PostMapping("/{cardId}/block")
    public void BlockCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal User user
    ) {
        cardService.blockCard(cardId, user.getId());
    }
    @PostMapping("/{cardId}/activate")
    public void ActivateCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal User user
    ) {
        cardService.activateCard(cardId, user.getId());
    }

    @PostMapping
    public CardResponse create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateCardRequest request
    ) {
        return cardService.create(
                userDetails.getUsername(),
                request.accountId()
        );
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cardId
    ) {
        cardService.delete(
                userDetails.getUsername(),
                cardId
        );
    }

}
