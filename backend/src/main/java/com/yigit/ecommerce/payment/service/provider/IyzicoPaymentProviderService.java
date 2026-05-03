package com.yigit.ecommerce.payment.service.provider;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.yigit.ecommerce.order.entity.Order;
import com.yigit.ecommerce.order.entity.OrderItem;
import com.yigit.ecommerce.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IyzicoPaymentProviderService {

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    public Payment makePayment(PaymentRequest request, Order order, BigDecimal totalAmount) {

        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);

        CreatePaymentRequest iyzicoRequest = new CreatePaymentRequest();
        iyzicoRequest.setLocale(Locale.TR.getValue());
        iyzicoRequest.setConversationId("order-" + order.getId());
        iyzicoRequest.setPrice(totalAmount);
        iyzicoRequest.setPaidPrice(totalAmount);
        iyzicoRequest.setCurrency(Currency.TRY.name());
        iyzicoRequest.setInstallment(1);
        iyzicoRequest.setBasketId("basket-" + order.getId());
        iyzicoRequest.setPaymentChannel(PaymentChannel.WEB.name());
        iyzicoRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCardHolderName());
        paymentCard.setCardNumber(request.getCardNumber());
        paymentCard.setExpireMonth(request.getExpireMonth());
        paymentCard.setExpireYear(request.getExpireYear());
        paymentCard.setCvc(request.getCvc());
        paymentCard.setRegisterCard(0);
        iyzicoRequest.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId("BY" + order.getId());
        buyer.setName("Yigit");
        buyer.setSurname("Turan");
        buyer.setGsmNumber("+905335909154");
        buyer.setEmail("test@test.com");
        buyer.setIdentityNumber("11111111110");
        buyer.setLastLoginDate("2026-05-03 12:00:00");
        buyer.setRegistrationDate("2026-05-03 12:00:00");
        buyer.setRegistrationAddress("Istanbul");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34000");
        iyzicoRequest.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName("Yigit Turan");
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Istanbul");
        shippingAddress.setZipCode("34000");
        iyzicoRequest.setShippingAddress(shippingAddress);

        Address billingAddress = new Address();
        billingAddress.setContactName("Yigit Turan");
        billingAddress.setCity("Istanbul");
        billingAddress.setCountry("Turkey");
        billingAddress.setAddress("Istanbul");
        billingAddress.setZipCode("34000");
        iyzicoRequest.setBillingAddress(billingAddress);

        List<BasketItem> basketItems = new ArrayList<>();

        for (OrderItem orderItem : order.getItems()) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(String.valueOf(orderItem.getProductId()));
            basketItem.setName(orderItem.getProductName());
            basketItem.setCategory1("General");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(orderItem.getTotalPrice());

            basketItems.add(basketItem);
        }

        iyzicoRequest.setBasketItems(basketItems);

        return Payment.create(iyzicoRequest, options);
    }
}