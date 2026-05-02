package com.yigit.ecommerce.order.controller;

import com.yigit.ecommerce.order.dto.OrderResponse;
import com.yigit.ecommerce.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/checkout/{cartId}")
    public OrderResponse checkout(@PathVariable Long cartId) {
        return orderService.checkout(cartId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }
}