package com.yigit.ecommerce.product.service.impl;

import com.yigit.ecommerce.product.dto.CreateProductRequest;
import com.yigit.ecommerce.product.dto.ProductResponse;
import com.yigit.ecommerce.product.entity.Product;
import com.yigit.ecommerce.product.repository.ProductRepository;
import com.yigit.ecommerce.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import com.yigit.ecommerce.exception.ProductNotFoundException;


@Service // Spring bunu otomatik tanır
@RequiredArgsConstructor // constructor otomatik oluşur
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        // DTO → Entity dönüşümü
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .build();

        // DB’ye kaydet
        Product savedProduct = productRepository.save(product);

        // Entity → Response dönüşümü
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı" + id));

        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı" + id));

        // güncelleme
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı. Id: " + id));

        productRepository.delete(product);
    }


    @Override
    public Page<ProductResponse> getAllProductsWithPagination(Pageable pageable) {

        // Repository'den sayfalı ürünleri alıyoruz
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // Entity → Response çevirme helper method
    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}