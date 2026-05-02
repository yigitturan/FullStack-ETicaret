package com.yigit.ecommerce.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // kullanicinin gorunen adi
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // sifreyi duz kaydetmeyecegiz, BCrypt ile encode edecegiz
    @Column(nullable = false)
    private String password;

    // simdilik USER ve ADMIN olarak ilerliyoruz
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.role == null) {
            this.role = Role.USER;
        }
    }
}