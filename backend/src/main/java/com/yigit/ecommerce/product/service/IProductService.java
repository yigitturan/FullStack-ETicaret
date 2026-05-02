package com.yigit.ecommerce.product.service;

import com.yigit.ecommerce.product.dto.CreateProductRequest;
import com.yigit.ecommerce.product.dto.ProductResponse;

import java.util.List;

public interface IProductService {

    // ürün oluştur
    ProductResponse createProduct(CreateProductRequest request);

    // tüm ürünleri getir
    List<ProductResponse> getAllProducts();

    // id ile ürün getir
    ProductResponse getProductById(Long id);

    // ürün güncelle
    ProductResponse updateProduct(Long id, CreateProductRequest request);

    // ürün sil
    void deleteProduct(Long id);
}