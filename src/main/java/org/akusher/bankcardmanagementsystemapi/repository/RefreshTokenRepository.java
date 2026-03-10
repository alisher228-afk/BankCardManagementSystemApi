package org.akusher.bankcardmanagementsystemapi.repository;

import org.akusher.bankcardmanagementsystemapi.entity.RefreshToken;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}