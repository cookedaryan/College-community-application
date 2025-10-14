package com.aryansinghdevelops.collegecommunitybackend.controller;

import com.aryansinghdevelops.collegecommunitybackend.dto.JwtResponse;
import com.aryansinghdevelops.collegecommunitybackend.dto.LoginRequest;
import com.aryansinghdevelops.collegecommunitybackend.dto.SignUpRequest;
import com.aryansinghdevelops.collegecommunitybackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
        String token = authService.register(signUpRequest);
        // FIXED: Return 201 Created status for successful resource creation
        return new ResponseEntity<>(new JwtResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}