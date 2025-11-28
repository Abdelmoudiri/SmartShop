package com.SmartShop.SmartShop.repositories;

import com.SmartShop.SmartShop.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalse();

    Page<Product> findByDeletedFalse(Pageable pageable);
    
    Page<Product> findByNomContainingIgnoreCaseAndDeletedFalse(String nom, Pageable pageable);
}