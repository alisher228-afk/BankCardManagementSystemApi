package org.akusher.bankcardmanagementsystemapi.controller;

import jakarta.validation.Valid;
import org.akusher.bankcardmanagementsystemapi.dto.AuthResponse;
import org.akusher.bankcardmanagementsystemapi.dto.LoginRequest;
import org.akusher.bankcardmanagementsystemapi.dto.RegisterRequest;
import org.akusher.bankcardmanagementsystemapi.entity.RefreshToken;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.entity.statusAndRole.Role;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.akusher.bankcardmanagementsystemapi.service.JwtService;
import org.akusher.bankcardmanagementsystemapi.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        String accessToken = jwtService.generateAccessToken(request.username());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());

        return ResponseEntity.ok(
                new AuthResponse(accessToken, refreshToken.getToken())
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        String requestRefreshToken = request.get("refreshToken");

        RefreshToken refreshToken = refreshTokenService.verify(requestRefreshToken);

        String newAccessToken =
                jwtService.generateAccessToken(refreshToken.getUser().getUsername());

        return ResponseEntity.ok(
                new AuthResponse(newAccessToken, requestRefreshToken)
        );
    }

}
