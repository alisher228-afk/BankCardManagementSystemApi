package org.akusher.bankcardmanagementsystemapi.repository;

import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.akusher.bankcardmanagementsystemapi.entity.CardStatus;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByAccountUser(User user, Pageable pageable);

    Page<Card> findByAccountUserAndStatus(User user, CardStatus status, Pageable pageable);

    boolean existsByAccountUserIdAndId(Long userId, Long cardId);
}