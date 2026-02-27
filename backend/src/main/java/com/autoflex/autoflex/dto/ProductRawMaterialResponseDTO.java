package com.autoflex.autoflex.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductRawMaterialResponseDTO(
        UUID id,
        String code,
        String name,
        BigDecimal price,
        List<ProductRawMaterialDTO> rawMaterials
) {
}
