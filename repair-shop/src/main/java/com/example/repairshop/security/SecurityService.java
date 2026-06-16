package com.example.repairshop.security;

import com.example.repairshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return userRepository.findByUsername(auth.getName())
                .map(u -> u.getId().equals(userId)).orElse(false);
    }
}