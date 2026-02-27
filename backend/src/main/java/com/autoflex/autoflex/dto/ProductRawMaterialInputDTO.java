package com.autoflex.autoflex.dto;

import java.util.UUID;

public record ProductRawMaterialInputDTO(
        UUID rawMaterialId,
        Integer requiredQuantity
) {
}
