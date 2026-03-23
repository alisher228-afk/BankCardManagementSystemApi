package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.user.UpdateUserRequest;
import org.akusher.bankcardmanagementsystemapi.dto.user.UserResponse;
import org.akusher.bankcardmanagementsystemapi.dto.mapping.UserResponseMapping;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserResponseMapping userResponseMapping;

    public UserService(UserRepository userRepository, UserResponseMapping userResponseMapping) {
        this.userRepository = userRepository;
        this.userResponseMapping = userResponseMapping;
    }

    public UserResponse getProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userResponseMapping.mapToResponse(user);
    }

    public UserResponse updateProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.username());
        user.setEmail(request.email());

        return userResponseMapping.mapToResponse(userRepository.save(user));
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository
                .findAll(pageable)
                .map(userResponseMapping::mapToResponse);
    }

    public UserResponse deleteUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
        return userResponseMapping.mapToResponse(user);
    }
}