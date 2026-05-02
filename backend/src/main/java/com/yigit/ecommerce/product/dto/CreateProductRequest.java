package com.yigit.ecommerce.product.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    // kullanıcıdan ürün oluştururken gelecek alanlar

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private String category;
}