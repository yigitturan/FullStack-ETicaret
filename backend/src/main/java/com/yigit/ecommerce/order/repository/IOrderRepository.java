package com.yigit.ecommerce.order.repository;

import com.yigit.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<Order, Long> {
}