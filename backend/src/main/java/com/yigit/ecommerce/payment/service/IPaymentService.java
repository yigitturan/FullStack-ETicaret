package com.yigit.ecommerce.payment.service;

import com.yigit.ecommerce.payment.dto.PaymentRequest;
import com.yigit.ecommerce.payment.dto.PaymentResponse;

public interface IPaymentService {

    PaymentResponse pay(PaymentRequest request);
}