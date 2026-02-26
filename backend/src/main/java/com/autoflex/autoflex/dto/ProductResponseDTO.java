package com.autoflex.autoflex.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String code,
        String name,
        BigDecimal price
) {
}
