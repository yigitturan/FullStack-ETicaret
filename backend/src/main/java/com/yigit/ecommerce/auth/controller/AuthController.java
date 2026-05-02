package com.yigit.ecommerce.auth.controller;

import com.yigit.ecommerce.auth.dto.AuthResponse;
import com.yigit.ecommerce.auth.dto.RegisterRequest;
import com.yigit.ecommerce.auth.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}