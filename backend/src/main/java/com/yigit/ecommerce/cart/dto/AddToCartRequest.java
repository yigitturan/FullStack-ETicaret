package com.yigit.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    @NotNull(message = "Ürün id boş olamaz")
    private Long productId;

    @NotNull(message = "Adet boş olamaz")
    @Positive(message = "Adet 0'dan büyük olmalıdır")
    private Integer quantity;
}