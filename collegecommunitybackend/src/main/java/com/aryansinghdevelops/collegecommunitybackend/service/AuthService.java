package com.aryansinghdevelops.collegecommunitybackend.service;

import com.aryansinghdevelops.collegecommunitybackend.dto.LoginRequest;
import com.aryansinghdevelops.collegecommunitybackend.dto.SignUpRequest;
import com.aryansinghdevelops.collegecommunitybackend.model.Role;
import com.aryansinghdevelops.collegecommunitybackend.model.User;
import com.aryansinghdevelops.collegecommunitybackend.repository.UserRepository;
import com.aryansinghdevelops.collegecommunitybackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user, saves them, and returns a JWT token.
     */
    public String register(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        // FIXED: Save the user and capture the returned, managed instance
        User savedUser = userRepository.save(user);

        // FIXED: Use the saved instance to generate the token
        return jwtService.generateToken(savedUser);
    }

    /**
     * Authenticates an existing user and returns a JWT token.
     */
    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        return jwtService.generateToken(user);
    }
}