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

import com.yigit.ecommerce.cart.dto.CartItemResponse;
import com.yigit.ecommerce.cart.dto.CartResponse;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // urun geldiginde artik urun sayisi kartta dogru sekilde  arttiriliyor
    @Override
    public void addToCart(AddToCartRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı. Id: " + request.getProductId()));

        Cart cart = cartRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> cartRepository.save(Cart.builder().build()));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
            return;
        }

        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(newCartItem);
    }

    // sepetten urun silinebilmeli
    @Override
    public void removeFromCart(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sepet ürünü bulunamadı"));

        cartItemRepository.delete(cartItem);
    }

    @Override
    public CartResponse getCart(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı. Id: " + cartId));

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(cart.getItems()
                        .stream()
                        .map(item -> CartItemResponse.builder()
                                .cartItemId(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .price(item.getProduct().getPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();
    }




}