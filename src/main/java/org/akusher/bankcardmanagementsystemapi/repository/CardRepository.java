package org.akusher.bankcardmanagementsystemapi.repository;

import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.akusher.bankcardmanagementsystemapi.entity.CardStatus;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwner(User owner, Pageable pageable);
    Page<Card> findByOwnerAndStatus(User owner, CardStatus status, Pageable pageable);
    boolean existsByOwnerIdAndId(Long ownerId, Long cardId);
}
