package com.example.repairshop.service;

import com.example.repairshop.dto.AuthRequest;
import com.example.repairshop.dto.AuthResponse;
import com.example.repairshop.entity.Role;
import com.example.repairshop.entity.User;
import com.example.repairshop.exception.UserAlreadyExistsException;
import com.example.repairshop.repository.RoleRepository;
import com.example.repairshop.repository.UserRepository;
import com.example.repairshop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }
        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token, "Bearer", request.getUsername());
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с именем '" + request.getUsername() + "' уже существует");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getUsername() + "@mail.com")
                .active(true)
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "Bearer", user.getUsername());
    }
}