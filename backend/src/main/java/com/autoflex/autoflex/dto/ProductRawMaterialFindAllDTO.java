package com.autoflex.autoflex.dto;

import java.util.List;
import java.util.UUID;

public record ProductRawMaterialFindAllDTO(
        UUID id,
        String code,
        String name,
        List<ProductRawMaterialDTO> rawMaterials
) {
}
