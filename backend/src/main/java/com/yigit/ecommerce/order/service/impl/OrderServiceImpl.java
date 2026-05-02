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
import lombok.RequiredArgsConstructor;
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

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı. Id: " + cartId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Sepet boş olduğu için sipariş oluşturulamaz");
        }

        Order order = Order.builder()
                .build();

        List<OrderItem> orderItems = cart.getItems()
                .stream()
                .map(cartItem -> mapCartItemToOrderItem(cartItem, order))
                .toList();

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı. Id: " + orderId));

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
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();


    }
}