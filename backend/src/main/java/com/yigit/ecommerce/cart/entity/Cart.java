package com.yigit.ecommerce.cart.entity;

import com.yigit.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    // her kullanicinin kendine ait sepeti olsun diye user ile bagliyorum
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // sepet silinirse icindeki urunler de silinsin diye cascade kullaniyorum
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}