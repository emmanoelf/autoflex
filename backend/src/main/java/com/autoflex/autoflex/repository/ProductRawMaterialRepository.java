package com.autoflex.autoflex.repository;

import com.autoflex.autoflex.model.ProductRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRawMaterialRepository extends JpaRepository<ProductRawMaterial, UUID> {
}
