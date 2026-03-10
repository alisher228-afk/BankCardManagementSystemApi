package org.akusher.bankcardmanagementsystemapi.service;

import org.akusher.bankcardmanagementsystemapi.dto.UpdateUserRequest;
import org.akusher.bankcardmanagementsystemapi.dto.UserResponse;
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

    public UserResponse updateProfile(User user, UpdateUserRequest request) {

        user.setUsername(request.username());
        user.setEmail(request.email());

        var updatedUser = userRepository.save(user);
        return userResponseMapping.mapToResponse(updatedUser);
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