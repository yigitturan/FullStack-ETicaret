package com.yigit.ecommerce.cart.repository;

import com.yigit.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICartRepository extends JpaRepository<Cart, Long> {

    // login olan kullanicinin sepetini bulmak icin kullaniyorum
    Optional<Cart> findByUserId(Long userId);
}