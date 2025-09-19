package com.ecom.product.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.product.dto.CreateProductRequest;
import com.ecom.product.dto.ProductDto;
import com.ecom.product.dto.UpdateProductRequest;
import com.ecom.product.entity.Product;
import com.ecom.product.exception.NotFoundException;
import com.ecom.product.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> listProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ProductDto getProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return toDto(p);
    }

    public ProductDto createProduct(CreateProductRequest req) {
        Product p = Product.builder()
                .sku(req.getSku())
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .currency(req.getCurrency())
                .category(req.getCategory())
                .build();
        Product saved = productRepository.save(p);
        return toDto(saved);
    }

    public ProductDto updateProduct(Long id, UpdateProductRequest req) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
        if (req.getTitle() != null) {
            p.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            p.setDescription(req.getDescription());
        }
        if (req.getPrice() != null) {
            p.setPrice(req.getPrice());
        }
        if (req.getCurrency() != null) {
            p.setCurrency(req.getCurrency());
        }
        if (req.getCategory() != null) {
            p.setCategory(req.getCategory());
        }
        p.setSku(req.getSku());
        Product saved = productRepository.save(p);
        return toDto(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .sku(p.getSku())
                .title(p.getTitle())
                .description(p.getDescription())
                .price(p.getPrice())
                .currency(p.getCurrency())
                .category(p.getCategory())
                .imageKey(p.getImageKey())
                .build();
    }

    public void setImageKey(Long productId, String key) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setImageKey(key);
        productRepository.save(product);
    }

    public String getImageKey(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        return product.getImageKey();
    }

}
