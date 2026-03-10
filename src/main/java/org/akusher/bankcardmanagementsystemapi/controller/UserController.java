package org.akusher.bankcardmanagementsystemapi.controller;

import org.akusher.bankcardmanagementsystemapi.dto.UpdateUserRequest;
import org.akusher.bankcardmanagementsystemapi.dto.UserResponse;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.akusher.bankcardmanagementsystemapi.repository.UserRepository;
import org.akusher.bankcardmanagementsystemapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails.getUsername());
    }

    @PutMapping("/me")
    public UserResponse updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateUserRequest request) {
        return userService.updateProfile(user, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deleteUser(@PathVariable Long userId) {
         return userService.deleteUser(userId);
    }
}
