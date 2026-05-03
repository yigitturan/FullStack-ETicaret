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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// burada gercek db kullanmiyoruz
// cartRepository ve orderRepository mock olacak
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private ICartRepository cartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkout_shouldCreateOrder_whenCartHasItems() {

        // login olan user gibi davranacak test user
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .role(Role.USER)
                .build();

        // product hazirliyoruz
        Product product = Product.builder()
                .id(1L)
                .name("Test Urun")
                .price(BigDecimal.valueOf(100))
                .build();

        // cart item hazirliyoruz
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        // cart hazirliyoruz
        Cart cart = Cart.builder()
                .id(1L)
                .items(List.of(cartItem))
                .build();

        // security context mockluyoruz
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContext.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

            // orderRepository save edilince iceri gelen order'i geri donduruyoruz
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            // checkout cagriliyor
            OrderResponse response = orderService.checkout(1L);

            // response kontrolleri
            assertNotNull(response);
            assertEquals(1L, response.getOrderId());
            assertEquals(1L, response.getUserId());
            assertEquals("user@test.com", response.getUserEmail());
            assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
            assertEquals(1, response.getItems().size());

            assertEquals("Test Urun", response.getItems().get(0).getProductName());
            assertEquals(2, response.getItems().get(0).getQuantity());
            assertEquals(BigDecimal.valueOf(200), response.getItems().get(0).getTotalPrice());

            verify(cartRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(Order.class));
        }
    }
}