package com.yigit.ecommerce.auth.service.impl;

import com.yigit.ecommerce.auth.dto.AuthResponse;
import com.yigit.ecommerce.auth.dto.RegisterRequest;
import com.yigit.ecommerce.auth.service.IAuthService;
import com.yigit.ecommerce.user.entity.Role;
import com.yigit.ecommerce.user.entity.User;
import com.yigit.ecommerce.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        // ayni email ile ikinci kez kayit olunmasin
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email zaten kayitli");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // token kismini bir sonraki adimda gercek JWT ile dolduracagiz
        return AuthResponse.builder()
                .token("register basarili - token sonra eklenecek")
                .build();
    }
}