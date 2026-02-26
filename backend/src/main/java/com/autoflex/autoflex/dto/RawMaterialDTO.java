package com.autoflex.autoflex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RawMaterialDTO(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull Integer stockQuantity
) {
}
