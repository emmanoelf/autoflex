package com.autoflex.autoflex.repository;

import com.autoflex.autoflex.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByCode(String code);
    Long deleteProductById(UUID productId);

    @Query("""
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.rawMaterials prm
            LEFT Join FETCH prm.rawMaterial
            ORDER BY p.price DESC
            """)
    List<Product> findAllProductsWithRawMaterialsOrderByPriceDesc();
}
