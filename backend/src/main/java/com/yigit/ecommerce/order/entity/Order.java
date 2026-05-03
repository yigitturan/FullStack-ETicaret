package com.yigit.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.yigit.ecommerce.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // sipariş ne zaman oluşturuldu
    private LocalDateTime createdAt;

    // sipariş durumu (şimdilik basit string)
    // ileride enum yapabiliriz
    private String status;

    // bir siparişin birden fazla item'ı olur
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // sipariş oluşturulurken otomatik çalışır
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "CREATED"; // default status
    }

    // bu order hangi kullaniciya ait onu tutuyoruz
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}