package com.yigit.ecommerce.cart.repository;

import com.yigit.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepository extends JpaRepository<Cart, Long> {
}