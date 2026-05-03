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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final IyzicoPaymentProviderService iyzicoPaymentProviderService;

    @Override
    public PaymentResponse pay(PaymentRequest request) {

        // odeme baslatiliyor, ilk log
        log.info("Odeme islemi basladi. orderId={}", request.getOrderId());

        // order var mi kontrol ediyoruz
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order bulunamadi. orderId={}", request.getOrderId());
                    return new RuntimeException("Order bulunamadi");
                });

        // order icindeki itemlardan toplam tutari hesapliyoruz
        BigDecimal totalAmount = order.getItems()
                .stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Order bulundu. orderId={}, totalAmount={}", order.getId(), totalAmount);

        // iyzico ya gercek api call atiyoruz
        com.iyzipay.model.Payment iyzicoPayment =
                iyzicoPaymentProviderService.makePayment(request, order, totalAmount);

        // iyzico dan gelen response
        log.info("Iyzico response alindi. orderId={}, status={}",
                order.getId(), iyzicoPayment.getStatus());

        // iyzico SUCCESS dondu mu kontrol
        boolean paymentSuccess = Status.SUCCESS.getValue().equals(iyzicoPayment.getStatus());

        if (paymentSuccess) {
            log.info("Odeme basarili. orderId={}", order.getId());
        } else {
            log.warn("Odeme basarisiz. orderId={}, hata={}",
                    order.getId(), iyzicoPayment.getErrorMessage());
        }

        // payment entity olusturuyoruz
        Payment payment = Payment.builder()
                .order(order)
                .amount(totalAmount)
                .provider(PaymentProvider.IYZICO)
                .status(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .message(paymentSuccess ? "Odeme basarili" : iyzicoPayment.getErrorMessage())
                .build();

        // db ye kaydediyoruz
        Payment savedPayment = paymentRepository.save(payment);

        // order status guncelleniyor
        if (paymentSuccess) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }

        orderRepository.save(order);

        log.info("Payment kaydedildi. paymentId={}, orderId={}, status={}",
                savedPayment.getId(), order.getId(), savedPayment.getStatus());

        // response donuyoruz
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