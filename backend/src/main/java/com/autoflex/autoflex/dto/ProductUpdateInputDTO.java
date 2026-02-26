package com.autoflex.autoflex.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateInputDTO(
        UUID id,
        String name,
        BigDecimal price
) {
}
