package com.yigit.ecommerce.cart.controller;

import com.yigit.ecommerce.cart.dto.AddToCartRequest;
import com.yigit.ecommerce.cart.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping("/add")
    public void addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
    }
}