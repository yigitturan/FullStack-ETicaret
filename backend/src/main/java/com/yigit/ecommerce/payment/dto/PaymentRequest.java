package com.yigit.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Order id bos olamaz")
    private Long orderId;

    @NotBlank(message = "Kart sahibi bos olamaz")
    private String cardHolderName;

    @NotBlank(message = "Kart numarasi bos olamaz")
    private String cardNumber;

    @NotBlank(message = "Ay bos olamaz")
    private String expireMonth;

    @NotBlank(message = "Yil bos olamaz")
    private String expireYear;

    @NotBlank(message = "CVC bos olamaz")
    private String cvc;
}