package com.yigit.ecommerce.product.controller;

import com.yigit.ecommerce.product.dto.CreateProductRequest;
import com.yigit.ecommerce.product.dto.ProductResponse;
import com.yigit.ecommerce.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

//import java.util.List;

@RestController // bu class artık API olacak
@RequestMapping("/api/products") // base url
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    // ürün oluştur
    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    // tüm ürünleri getir ESKI------
    //@GetMapping
    //public List<ProductResponse> getAllProducts() {
    //    return productService.getAllProducts();
    //}

    // ürünleri sayfalı getir
    @GetMapping("/page")
    public Page<ProductResponse> getProductsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProductsWithPagination(pageable);
    }

    // id ile ürün getir
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ürün güncelle
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody CreateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // ürün sil
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}