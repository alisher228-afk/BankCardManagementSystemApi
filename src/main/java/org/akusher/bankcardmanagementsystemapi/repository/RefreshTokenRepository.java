package org.akusher.bankcardmanagementsystemapi.repository;

import org.akusher.bankcardmanagementsystemapi.entity.RefreshToken;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Modifying
    @Query("delete from RefreshToken r where r.user.id = :userId")
    void deleteByUserId(Long userId);
}