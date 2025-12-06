package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.ProductDTO;
import com.SmartShop.SmartShop.entities.Product;
import com.SmartShop.SmartShop.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService service;

    private  Product p1;
    private ProductDTO.CreateRequest request;

    @BeforeEach
    public void init() {
        p1 = Product.builder()
                .id(1L)
                .nom("maticha")
                .prixUnitaire(BigDecimal.valueOf(100))
                .stock(50)
                .deleted(false)
                .build();

        request = ProductDTO.CreateRequest.builder()
                .nom("maticha")
                .prixUnitaire(BigDecimal.valueOf(100))
                .stock(50)
                .build();
    }


    @Test
    void createProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(p1);

        // Act
        Product p2 = service.createProduct(request);

        // Assert
        assertNotNull(p2);
        assertEquals(1L, p2.getId());
        assertEquals("maticha", p2.getNom());
        assertEquals(50, p2.getStock());
        assertEquals(BigDecimal.valueOf(100), p2.getPrixUnitaire());
        assertFalse(p2.isDeleted());

        verify(productRepository).save(any(Product.class));
    }
}