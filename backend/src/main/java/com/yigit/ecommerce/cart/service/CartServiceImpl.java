package com.yigit.ecommerce.cart.service.impl;

import com.yigit.ecommerce.cart.dto.AddToCartRequest;
import com.yigit.ecommerce.cart.entity.Cart;
import com.yigit.ecommerce.cart.entity.CartItem;
import com.yigit.ecommerce.cart.repository.ICartItemRepository;
import com.yigit.ecommerce.cart.repository.ICartRepository;
import com.yigit.ecommerce.cart.service.ICartService;
import com.yigit.ecommerce.exception.ProductNotFoundException;
import com.yigit.ecommerce.product.entity.Product;
import com.yigit.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public void addToCart(AddToCartRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı. Id: " + request.getProductId()));

        Cart cart = Cart.builder()
                .build();

        Cart savedCart = cartRepository.save(cart);

        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(cartItem);
    }
}