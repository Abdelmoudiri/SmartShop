package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.ProductDTO;
import com.SmartShop.SmartShop.entities.Product;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.exceptions.ResourceNotFoundException;
import com.SmartShop.SmartShop.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    @Transactional
    public Product createProduct(ProductDTO.CreateRequest request) {
        Product product = Product.builder()
                .nom(request.getNom())
                .prixUnitaire(request.getPrixUnitaire())
                .stock(request.getStock())
                .deleted(false)
                .build();

        return productRepository.save(product);
    }


    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));
    }


    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalse(pageable);
    }


    public Page<Product> searchProducts(String nom, Pageable pageable) {
        return productRepository.findByNomContainingIgnoreCaseAndDeletedFalse(nom, pageable);
    }


    @Transactional
    public Product updateProduct(Long id, ProductDTO.UpdateRequest request) {
        Product product = getProductById(id);

        if (request.getNom() != null) {
            product.setNom(request.getNom());
        }

        if (request.getPrixUnitaire() != null) {
            if (request.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Le prix unitaire doit être supérieur à 0");
            }
            product.setPrixUnitaire(request.getPrixUnitaire());
        }

        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                throw new BusinessException("Le stock ne peut pas être négatif");
            }
            product.setStock(request.getStock());
        }

        return productRepository.save(product);
    }


    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        

        product.setDeleted(true);
        productRepository.save(product);
    }


    @Transactional
    public void validateAndReserveStock(Long productId, int quantityRequested) {
        Product product = getProductById(productId);

        if (product.getStock() < quantityRequested) {
            throw new BusinessException(
                String.format("Stock insuffisant pour le produit '%s'. Disponible: %d, Demandé: %d",
                    product.getNom(), product.getStock(), quantityRequested)
            );
        }

        product.setStock(product.getStock() - quantityRequested);
        productRepository.save(product);
    }


    @Transactional
    public void restoreStock(Long productId, int quantityToRestore) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + productId));

        product.setStock(product.getStock() + quantityToRestore);
        productRepository.save(product);
    }
}
