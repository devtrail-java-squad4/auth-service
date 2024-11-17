package com.acabouomony.auth.service.impl;

import com.acabouomony.auth.dto.AuthRequest;
import com.acabouomony.auth.dto.AuthResponse;
import com.acabouomony.auth.dto.UserRegistrationRequest;
import com.acabouomony.auth.model.User;
import com.acabouomony.auth.repository.UserRepository;
import com.acabouomony.auth.service.AuthService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String jwtSecret = "${JWT_SECRET}";
    private final long jwtExpirationInMs = 3600000; // 1 hour

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername());
        if (user != null && passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            String token = JWT.create()
                    .withSubject(user.getUsername())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .sign(Algorithm.HMAC512(jwtSecret));
            return new AuthResponse(token);
        }
        throw new RuntimeException("Invalid username or password");
    }

    @Override
    public void registerUser(UserRegistrationRequest userRegistrationRequest) {
        User user = new User();
        user.setUsername(userRegistrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
    }

    @Override
    public AuthResponse renewToken(String oldToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(oldToken);
            String username = decodedJWT.getSubject();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                String newToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withIssuedAt(new Date())
                        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                        .sign(algorithm);
                return new AuthResponse(newToken);
            }
            throw new RuntimeException("User not found");
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }
}