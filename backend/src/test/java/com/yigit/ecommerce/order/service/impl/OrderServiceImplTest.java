package com.yigit.ecommerce.order.service.impl;

import com.yigit.ecommerce.cart.entity.Cart;
import com.yigit.ecommerce.cart.entity.CartItem;
import com.yigit.ecommerce.cart.repository.ICartRepository;
import com.yigit.ecommerce.order.dto.OrderResponse;
import com.yigit.ecommerce.order.entity.Order;
import com.yigit.ecommerce.order.repository.IOrderRepository;
import com.yigit.ecommerce.product.entity.Product;
import com.yigit.ecommerce.user.entity.Role;
import com.yigit.ecommerce.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// burada gercek db kullanmiyoruz
// repository siniflari mock olacak
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private ICartRepository cartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkout_shouldCreateOrder_whenCartHasItems() {
        User user = createUser(1L, "user@test.com", Role.USER);
        setLoggedInUser(user);

        Product product = createProduct(1L, "Test Urun", BigDecimal.valueOf(100));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .items(List.of(cartItem))
                .build();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderService.checkout(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getUserId());
        assertEquals("user@test.com", response.getUserEmail());
        assertEquals("CREATED", response.getStatus());
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(1, response.getItems().size());

        assertEquals(1L, response.getItems().get(0).getProductId());
        assertEquals("Test Urun", response.getItems().get(0).getProductName());
        assertEquals(BigDecimal.valueOf(100), response.getItems().get(0).getPrice());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(200), response.getItems().get(0).getTotalPrice());

        verify(cartRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void checkout_shouldThrowException_whenCartIsEmpty() {
        User user = createUser(1L, "user@test.com", Role.USER);
        setLoggedInUser(user);

        Cart cart = Cart.builder()
                .id(1L)
                .items(List.of())
                .build();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.checkout(1L)
        );

        assertEquals("Sepet bos oldugu icin siparis olusturulamaz", exception.getMessage());

        verify(cartRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void checkoutMyCart_shouldCreateOrder_whenUserCartHasItems() {
        User user = createUser(1L, "user@test.com", Role.USER);
        setLoggedInUser(user);

        Product product = createProduct(10L, "Mouse", BigDecimal.valueOf(500));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(3)
                .build();

        Cart cart = Cart.builder()
                .id(5L)
                .items(List.of(cartItem))
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(20L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderService.checkoutMyCart();

        assertNotNull(response);
        assertEquals(20L, response.getOrderId());
        assertEquals(1L, response.getUserId());
        assertEquals(BigDecimal.valueOf(1500), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        assertEquals("Mouse", response.getItems().get(0).getProductName());

        verify(cartRepository, times(1)).findByUserId(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderBelongsToCurrentUser() {
        User user = createUser(1L, "user@test.com", Role.USER);
        setLoggedInUser(user);

        Product product = createProduct(1L, "Klavye", BigDecimal.valueOf(1000));

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .items(List.of(
                        com.yigit.ecommerce.order.entity.OrderItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .price(product.getPrice())
                                .quantity(1)
                                .totalPrice(product.getPrice())
                                .build()
                ))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getUserId());
        assertEquals("user@test.com", response.getUserEmail());
        assertEquals(BigDecimal.valueOf(1000), response.getTotalAmount());

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_shouldThrowException_whenUserTriesAnotherUsersOrder() {
        User currentUser = createUser(1L, "user@test.com", Role.USER);
        User orderOwner = createUser(2L, "other@test.com", Role.USER);

        setLoggedInUser(currentUser);

        Order order = Order.builder()
                .id(1L)
                .user(orderOwner)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.getOrderById(1L)
        );

        assertEquals("Bu siparise erisim yetkin yok", exception.getMessage());

        verify(orderRepository, times(1)).findById(1L);
    }

    private void setLoggedInUser(User user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User createUser(Long id, String email, Role role) {
        return User.builder()
                .id(id)
                .email(email)
                .role(role)
                .build();
    }

    private Product createProduct(Long id, String name, BigDecimal price) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }
}