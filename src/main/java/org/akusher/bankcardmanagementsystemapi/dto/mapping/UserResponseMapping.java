package org.akusher.bankcardmanagementsystemapi.dto.mapping;

import org.akusher.bankcardmanagementsystemapi.dto.user.UserResponse;
import org.akusher.bankcardmanagementsystemapi.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapping {
    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
