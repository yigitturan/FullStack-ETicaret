package com.yigit.ecommerce.cart.service;

import com.yigit.ecommerce.cart.dto.AddToCartRequest;

public interface ICartService {

    void addToCart(AddToCartRequest request);
}