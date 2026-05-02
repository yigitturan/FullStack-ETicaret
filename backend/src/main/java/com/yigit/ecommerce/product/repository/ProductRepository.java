package com.yigit.ecommerce.product.repository;

import com.yigit.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Aktif ürünleri listelemek için kullanacağız
    List<Product> findByActiveTrue();

    // Kategoriye göre aktif ürünleri getirmek için
    List<Product> findByCategoryAndActiveTrue(String category);
}