package com.yigit.ecommerce.order.service;

import com.yigit.ecommerce.order.dto.OrderResponse;

public interface IOrderService {

    OrderResponse checkout(Long cartId);

    // YENI
    OrderResponse checkoutMyCart();

    OrderResponse getOrderById(Long orderId);
}