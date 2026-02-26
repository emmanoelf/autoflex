package com.autoflex.autoflex.repository;

import com.autoflex.autoflex.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByCode(String code);
    Long deleteProductById(UUID productId);
}
