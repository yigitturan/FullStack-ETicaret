package com.yigit.ecommerce.order.service;

import com.yigit.ecommerce.order.dto.OrderResponse;

public interface IOrderService {

    OrderResponse checkout(Long cartId);

    OrderResponse getOrderById(Long orderId);
}