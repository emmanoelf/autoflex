package com.autoflex.autoflex.dto;

import java.util.UUID;

public record ProductRawMaterialDTO(
        UUID productRawMaterialId,
        UUID rawMaterialId,
        String rawMaterialName,
        Integer stockQuantity,
        Integer requiredQuantity
) {
}
