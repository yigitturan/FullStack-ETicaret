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

import com.yigit.ecommerce.auth.dto.LoginRequest;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //register
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


    //login
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));

        // girilen sifre ile veritabanindaki sifre esit mi kontrol ediyoruz
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email veya sifre hatali");
        }

        // JWT tokeni bir sonraki adimda burada uretecegiz
        return AuthResponse.builder()
                .token("login basarili - token sonra eklenecek")
                .build();
    }


}