package com.yigit.ecommerce.order.controller;

import com.yigit.ecommerce.order.dto.OrderResponse;
import com.yigit.ecommerce.order.service.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IOrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OrderController orderController = new OrderController(orderService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    void checkoutMyCart_shouldReturnOrderResponse() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .userId(1L)
                .userEmail("user@test.com")
                .status("CREATED")
                .totalAmount(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderService.checkoutMyCart()).thenReturn(response);

        mockMvc.perform(post("/api/orders/checkout/my-cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.userEmail").value("user@test.com"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(200));

        verify(orderService, times(1)).checkoutMyCart();
    }

    @Test
    void getOrderById_shouldReturnOrderResponse() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .orderId(5L)
                .userId(1L)
                .userEmail("user@test.com")
                .status("CREATED")
                .totalAmount(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderService.getOrderById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(5L))
                .andExpect(jsonPath("$.totalAmount").value(500));

        verify(orderService, times(1)).getOrderById(5L);
    }
}