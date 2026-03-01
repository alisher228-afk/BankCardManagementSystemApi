package org.akusher.bankcardmanagementsystemapi.repository;

import org.akusher.bankcardmanagementsystemapi.entity.Card;
import org.akusher.bankcardmanagementsystemapi.entity.CardStatus;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    // Получить карты по владельцу аккаунта
    Page<Card> findByAccountUser(User user, Pageable pageable);

    // Получить карты по владельцу аккаунта и статусу карты
    Page<Card> findByAccountUserAndStatus(User user, CardStatus status, Pageable pageable);

    // Проверить, существует ли карта с указанным владельцем и id карты
    boolean existsByAccountUserIdAndId(Long userId, Long cardId);
}