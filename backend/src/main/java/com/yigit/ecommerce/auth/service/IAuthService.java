package com.yigit.ecommerce.auth.service;

import com.yigit.ecommerce.auth.dto.AuthResponse;
import com.yigit.ecommerce.auth.dto.LoginRequest;
import com.yigit.ecommerce.auth.dto.RegisterRequest;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}