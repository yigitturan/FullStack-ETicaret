package com.yigit.ecommerce.order.service.impl;

import com.yigit.ecommerce.cart.entity.Cart;
import com.yigit.ecommerce.cart.entity.CartItem;
import com.yigit.ecommerce.cart.repository.ICartRepository;
import com.yigit.ecommerce.order.dto.OrderItemResponse;
import com.yigit.ecommerce.order.dto.OrderResponse;
import com.yigit.ecommerce.order.entity.Order;
import com.yigit.ecommerce.order.entity.OrderItem;
import com.yigit.ecommerce.order.repository.IOrderRepository;
import com.yigit.ecommerce.order.service.IOrderService;
import com.yigit.ecommerce.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final ICartRepository cartRepository;

    @Override
    public OrderResponse checkout(Long cartId) {

        // login olan kullaniciyi security contextten aliyoruz
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // once sepet var mi onu buluyoruz
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadi. Id: " + cartId));

        // sepet bos ise siparis olusturmuyoruz
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Sepet bos oldugu icin siparis olusturulamaz");
        }

        // once order nesnesini olusturuyoruz
        // itemlari birazdan set edecegiz cunku itemlar order referansina ihtiyac duyuyor
        Order order = Order.builder()
                .user(currentUser)
                .build();

        // cart itemlari order item'a ceviriyoruz
        // burada urun bilgilerini snapshot olarak aliyoruz
        List<OrderItem> orderItems = cart.getItems()
                .stream()
                .map(cartItem -> mapCartItemToOrderItem(cartItem, order))
                .toList();

        // order ile itemlari birbirine bagliyoruz
        order.setItems(orderItems);

        // order db'ye kaydediliyor
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        // login olan kullaniciyi aliyoruz
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // order var mi kontrol ediyoruz
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Siparis bulunamadi. Id: " + orderId));

        // admin herkesin siparisini gorebilir
        // user sadece kendi siparisini gorebilir
        if (!order.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Bu siparise erisim yetkin yok");
        }

        return mapToResponse(order);
    }

    private OrderItem mapCartItemToOrderItem(CartItem cartItem, Order order) {

        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return OrderItem.builder()
                .order(order)
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .price(price)
                .quantity(cartItem.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        BigDecimal totalAmount = itemResponses.stream()
                .map(OrderItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .status(order.getStatus())
                .totalAmount(totalAmount)
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    @Override
    public OrderResponse checkoutMyCart() {

        // login olan user
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // kullanicinin cartini buluyoruz
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Kullaniciya ait sepet bulunamadi"));

        // sepet bos mu kontrol
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Sepet bos oldugu icin siparis olusturulamaz");
        }

        Order order = Order.builder()
                .user(currentUser)
                .build();

        List<OrderItem> orderItems = cart.getItems()
                .stream()
                .map(cartItem -> mapCartItemToOrderItem(cartItem, order))
                .toList();

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }


}