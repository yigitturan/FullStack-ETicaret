package com.yigit.ecommerce.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Ad soyad bos olamaz")
    private String fullName;

    @NotBlank(message = "Email bos olamaz")
    @Email(message = "Gecerli bir email giriniz")
    private String email;

    @NotBlank(message = "Sifre bos olamaz")
    private String password;
}