package com.yigit.ecommerce.payment.service.impl;

import com.iyzipay.model.Status;
import com.yigit.ecommerce.order.entity.Order;
import com.yigit.ecommerce.order.repository.IOrderRepository;
import com.yigit.ecommerce.payment.dto.PaymentRequest;
import com.yigit.ecommerce.payment.dto.PaymentResponse;
import com.yigit.ecommerce.payment.entity.Payment;
import com.yigit.ecommerce.payment.enums.PaymentProvider;
import com.yigit.ecommerce.payment.enums.PaymentStatus;
import com.yigit.ecommerce.payment.repository.IPaymentRepository;
import com.yigit.ecommerce.payment.service.IPaymentService;
import com.yigit.ecommerce.payment.service.provider.IyzicoPaymentProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final IyzicoPaymentProviderService iyzicoPaymentProviderService;

    @Override
    public PaymentResponse pay(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order bulunamadi"));

        BigDecimal totalAmount = order.getItems()
                .stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        com.iyzipay.model.Payment iyzicoPayment =
                iyzicoPaymentProviderService.makePayment(request, order, totalAmount);

        boolean paymentSuccess = Status.SUCCESS.getValue().equals(iyzicoPayment.getStatus());

        Payment payment = Payment.builder()
                .order(order)
                .amount(totalAmount)
                .provider(PaymentProvider.IYZICO)
                .status(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .message(paymentSuccess ? "Odeme basarili" : iyzicoPayment.getErrorMessage())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        if (paymentSuccess) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }

        orderRepository.save(order);

        return PaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .orderId(order.getId())
                .amount(savedPayment.getAmount())
                .provider(savedPayment.getProvider().name())
                .status(savedPayment.getStatus().name())
                .message(savedPayment.getMessage())
                .createdAt(savedPayment.getCreatedAt())
                .build();
    }
}