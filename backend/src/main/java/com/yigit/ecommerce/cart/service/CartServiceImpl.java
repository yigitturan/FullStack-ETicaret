package com.yigit.ecommerce.cart.service.impl;

import com.yigit.ecommerce.cart.dto.AddToCartRequest;
import com.yigit.ecommerce.cart.dto.CartItemResponse;
import com.yigit.ecommerce.cart.dto.CartResponse;
import com.yigit.ecommerce.cart.entity.Cart;
import com.yigit.ecommerce.cart.entity.CartItem;
import com.yigit.ecommerce.cart.repository.ICartItemRepository;
import com.yigit.ecommerce.cart.repository.ICartRepository;
import com.yigit.ecommerce.cart.service.ICartService;
import com.yigit.ecommerce.exception.ProductNotFoundException;
import com.yigit.ecommerce.product.entity.Product;
import com.yigit.ecommerce.product.repository.ProductRepository;
import com.yigit.ecommerce.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public void addToCart(AddToCartRequest request) {

        User currentUser = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı. Id: " + request.getProductId()));

        // kullanicinin sepeti varsa onu aliyorum, yoksa yeni sepet olusturuyorum
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(currentUser)
                                .items(new ArrayList<>())
                                .build()
                ));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        // urun zaten sepette varsa adetini arttiriyorum
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
            return;
        }

        // urun sepette yoksa yeni cart item olarak ekliyorum
        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(newCartItem);
    }

    @Override
    public void removeFromCart(Long cartItemId) {

        User currentUser = getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sepet ürünü bulunamadı"));

        // kullanici baskasinin sepetindeki urunu silemesin diye kontrol ediyorum
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bu sepet urununu silme yetkin yok");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public CartResponse getCart(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı. Id: " + cartId));

        return convertToCartResponse(cart);
    }

    @Override
    public CartResponse getMyCart() {

        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(currentUser)
                                .items(new ArrayList<>())
                                .build()
                ));

        return convertToCartResponse(cart);
    }

    private CartResponse convertToCartResponse(Cart cart) {

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

    private User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new RuntimeException("Kullanici bilgisi bulunamadi");
    }
}