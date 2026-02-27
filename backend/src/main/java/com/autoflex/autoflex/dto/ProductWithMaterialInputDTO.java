package com.autoflex.autoflex.dto;

import java.util.List;
import java.util.UUID;

public record ProductWithMaterialInputDTO(
        UUID productId,
        List<ProductRawMaterialInputDTO> rawMaterials
) {
}
