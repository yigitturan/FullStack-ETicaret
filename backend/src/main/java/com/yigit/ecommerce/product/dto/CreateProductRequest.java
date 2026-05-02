package com.yigit.ecommerce.product.dto;

import lombok.*;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    // kullanıcıdan ürün oluştururken gelecek alanlar

    @NotBlank(message = "urun adi bos olamaz")
    private String name;

    @Size(max=1000, message = " urun aciklamasi en fazla 1000 karakter olabilir ")
    private String description;

    @NotNull(message = "urun fiyati bos olamaz")
    @Positive(message = "urun fiyati 0 dan buyuk olmalidir")
    private BigDecimal price;

    @NotNull(message = "Stok miktari bos olamaz")
    @PositiveOrZero(message = "Stok miktari negati olamaz")
    private Integer stockQuantity;

    private String imageUrl;

    @NotBlank(message = "Kategori bos olamaz")
    private String category;
}