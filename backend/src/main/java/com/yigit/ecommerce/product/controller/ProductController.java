package com.yigit.ecommerce.product.controller;

import com.yigit.ecommerce.product.dto.CreateProductRequest;
import com.yigit.ecommerce.product.dto.ProductResponse;
import com.yigit.ecommerce.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // bu class artık API olacak
@RequestMapping("/api/products") // base url
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    // ürün oluştur
    @PostMapping
    public ProductResponse createProduct(@RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    // tüm ürünleri getir
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // id ile ürün getir
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ürün güncelle
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,
                                         @RequestBody CreateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // ürün sil
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}