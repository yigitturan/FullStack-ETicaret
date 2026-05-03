package com.yigit.ecommerce.payment.service.impl;

import com.iyzipay.model.Status;
import com.yigit.ecommerce.order.entity.Order;
import com.yigit.ecommerce.order.entity.OrderItem;
import com.yigit.ecommerce.order.repository.IOrderRepository;
import com.yigit.ecommerce.payment.dto.PaymentRequest;
import com.yigit.ecommerce.payment.dto.PaymentResponse;
import com.yigit.ecommerce.payment.entity.Payment;
import com.yigit.ecommerce.payment.enums.PaymentProvider;
import com.yigit.ecommerce.payment.enums.PaymentStatus;
import com.yigit.ecommerce.payment.repository.IPaymentRepository;
import com.yigit.ecommerce.payment.service.provider.IyzicoPaymentProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// burada gercek db veya gercek iyzico kullanmiyoruz
// hepsini mocklayip sadece PaymentService logic'ini test ediyoruz
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IyzicoPaymentProviderService iyzicoPaymentProviderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void pay_shouldReturnSuccess_whenIyzicoPaymentSuccess() {

        // test icin request hesabi
        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .cardHolderName("John Doe")
                .cardNumber("5528790033300008")
                .expireMonth("12")
                .expireYear("2030")
                .cvc("123")
                .build();

        // order item hazirlama
        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .productName("Test Urun")
                .price(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        // order hazirliyoruz
        Order order = Order.builder()
                .id(1L)
                .status("CREATED")
                .items(List.of(orderItem))
                .build();

        // iyzico response mock
        com.iyzipay.model.Payment iyzicoPayment = new com.iyzipay.model.Payment();
        iyzicoPayment.setStatus(Status.SUCCESS.getValue());

        // db ye kayitli gibi payment hazirlama
        Payment savedPayment = Payment.builder()
                .id(1L)
                .order(order)
                .amount(BigDecimal.valueOf(100))
                .provider(PaymentProvider.IYZICO)
                .status(PaymentStatus.SUCCESS)
                .message("Odeme basarili")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(iyzicoPaymentProviderService.makePayment(any(), any(), any())).thenReturn(iyzicoPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // servis cagr
        PaymentResponse response = paymentService.pay(request);

        // sonuc kontrol
        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals(1L, response.getOrderId());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("IYZICO", response.getProvider());
        assertEquals("Odeme basarili", response.getMessage());

        // order status PAID oldu mu kontrol ediyoruz
        assertEquals("PAID", order.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(iyzicoPaymentProviderService, times(1)).makePayment(any(), any(), any());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void pay_shouldReturnFailed_whenIyzicoPaymentFailed() {

        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .cardHolderName("John Doe")
                .cardNumber("5528790000000008")
                .expireMonth("12")
                .expireYear("2030")
                .cvc("123")
                .build();

        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .productName("Test Urun")
                .price(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        Order order = Order.builder()
                .id(1L)
                .status("CREATED")
                .items(List.of(orderItem))
                .build();

        com.iyzipay.model.Payment iyzicoPayment = new com.iyzipay.model.Payment();
        iyzicoPayment.setStatus("failure");
        iyzicoPayment.setErrorMessage("Odeme reddedildi");

        Payment savedPayment = Payment.builder()
                .id(2L)
                .order(order)
                .amount(BigDecimal.valueOf(100))
                .provider(PaymentProvider.IYZICO)
                .status(PaymentStatus.FAILED)
                .message("Odeme reddedildi")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(iyzicoPaymentProviderService.makePayment(any(), any(), any())).thenReturn(iyzicoPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentResponse response = paymentService.pay(request);

        assertNotNull(response);
        assertEquals(2L, response.getPaymentId());
        assertEquals("FAILED", response.getStatus());
        assertEquals("Odeme reddedildi", response.getMessage());

        // basarisiz odemede order status PAYMENT_FAILED olmali
        assertEquals("PAYMENT_FAILED", order.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(iyzicoPaymentProviderService, times(1)).makePayment(any(), any(), any());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
    }
}