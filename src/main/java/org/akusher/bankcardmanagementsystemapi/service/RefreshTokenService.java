package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.entity.RefreshToken;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.repository.RefreshTokenRepository;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository repository, JwtService jwtService, UserRepository userRepository) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        repository.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(jwtService.generateRefreshToken(username));
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        return repository.save(token);
    }

    public RefreshToken verify(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            repository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}