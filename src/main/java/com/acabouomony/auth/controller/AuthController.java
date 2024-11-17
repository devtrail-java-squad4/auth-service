package com.acabouomony.auth.controller;

import com.acabouomony.auth.dto.AuthRequest;
import com.acabouomony.auth.dto.AuthResponse;
import com.acabouomony.auth.dto.UserRegistrationRequest;
import com.acabouomony.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        authService.registerUser(userRegistrationRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> renewToken(@RequestBody String oldToken) {
        AuthResponse authResponse = authService.renewToken(oldToken);
        return ResponseEntity.ok(authResponse);
    }
}