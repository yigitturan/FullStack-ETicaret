package com.yigit.ecommerce.cart.service;

import com.yigit.ecommerce.cart.dto.AddToCartRequest;
import com.yigit.ecommerce.cart.dto.CartResponse;

public interface ICartService {

    void addToCart(AddToCartRequest request);

    void removeFromCart(Long cartItemId);

    CartResponse getCart(Long cartId);
}