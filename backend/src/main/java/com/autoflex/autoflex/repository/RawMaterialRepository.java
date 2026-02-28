package com.autoflex.autoflex.repository;


import com.autoflex.autoflex.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, UUID> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID rawMaterialId);
    Long deleteRawMaterialById(UUID rawMaterialId);
}
