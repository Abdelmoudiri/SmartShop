package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.ProductDTO;
import com.SmartShop.SmartShop.entities.Product;
import com.SmartShop.SmartShop.mapper.ProductMapper;
import com.SmartShop.SmartShop.services.ProductService;
import com.SmartShop.SmartShop.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductDTO.Response> createProduct(
            @Valid @RequestBody ProductDTO.CreateRequest request) {
        
        SecurityUtil.requireAdmin();

        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO.Response>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(products.map(productMapper::toResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO.Response>> searchProducts(
            @RequestParam String nom,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nom"));
        Page<Product> products = productService.searchProducts(nom, pageable);
        
        return ResponseEntity.ok(products.map(productMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateRequest request) {
        
        SecurityUtil.requireAdmin();

        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        SecurityUtil.requireAdmin();

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
