package com.yigit.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity // Bu class artık database'te tabloya karşılık gelecek
@Table(name = "products") // Tablo adı
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id otomatik artsın
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000) // açıklama uzun olabilir diye limit koyduk
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    // ürün görseli için url tutuyoruz
    private String imageUrl;

    // kategori basit string olarak başlıyoruz (ileride ayrı tablo yapabiliriz)
    private String category;

    // ürün aktif mi pasif mi (silmek yerine pasife çekmek daha mantıklı)
    private Boolean active;

    // ürün ne zaman oluşturuldu
    private LocalDateTime createdAt;

    // en son ne zaman güncellendi
    private LocalDateTime updatedAt;

    // kayıt ilk oluşurken burası otomatik çalışır
    @PrePersist
    public void prePersist() {
        this.active = true; // varsayılan aktif
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // update olunca sadece updatedAt değişsin
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}