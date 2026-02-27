package com.autoflex.autoflex.dto;

import java.util.UUID;

public record ProductRawMaterialDTO(
        UUID id,
        String rawMaterialName,
        Integer stockQuantity,
        Integer requiredQuantity
) {
}
