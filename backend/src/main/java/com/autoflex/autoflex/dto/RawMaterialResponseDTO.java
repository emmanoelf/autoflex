package com.autoflex.autoflex.dto;

import java.util.UUID;

public record RawMaterialResponseDTO(
        UUID id,
        String code,
        String name,
        Integer stockQuantity
) {
}
