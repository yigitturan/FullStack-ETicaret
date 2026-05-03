package com.yigit.ecommerce.payment.controller;

import com.yigit.ecommerce.payment.dto.PaymentRequest;
import com.yigit.ecommerce.payment.dto.PaymentResponse;
import com.yigit.ecommerce.payment.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(request);
    }
}