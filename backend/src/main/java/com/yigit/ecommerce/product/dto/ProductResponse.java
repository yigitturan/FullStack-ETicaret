package com.yigit.ecommerce.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    // frontend'e döneceğimiz alanlar

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private String category;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}