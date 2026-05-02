package com.yigit.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // product id'yi saklıyoruz ama direkt relation kurmuyoruz
    // çünkü sipariş geçmişi değişmemeli
    // yani burada cart tarafindan farkli bir yapi var, kart yani sepet -> referans
    // ama order -> kullanilan o an ki deger yani history mantigi  !! bunu unutmamaliyim
    private Long productId;

    // ürün adı snapshot olarak tutulur
    private String productName;

    // ürün fiyatı snapshot (çok kritik)
    private BigDecimal price;

    // kaç adet alındı
    private Integer quantity;

    // toplam fiyat (price * quantity)
    private BigDecimal totalPrice;

    // order ile ilişki
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}