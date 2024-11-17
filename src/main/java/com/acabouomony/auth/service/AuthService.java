package com.acabouomony.auth.service;

import com.acabouomony.auth.dto.AuthRequest;
import com.acabouomony.auth.dto.AuthResponse;
import com.acabouomony.auth.dto.UserRegistrationRequest;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);
    void registerUser(UserRegistrationRequest userRegistrationRequest);
    AuthResponse renewToken(String oldToken);
}
