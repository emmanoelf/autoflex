package com.autoflex.autoflex.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductAvailableProductionDTO(
        UUID productId,
        String code,
        String name,
        BigDecimal price,
        int quantityProductionAvailable,
        BigDecimal totalValue
) {
}
