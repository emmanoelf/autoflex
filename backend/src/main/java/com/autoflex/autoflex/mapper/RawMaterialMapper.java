package com.autoflex.autoflex.mapper;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.autoflex.model.RawMaterial;

public class RawMaterialMapper {

    public static RawMaterial toModel(RawMaterialDTO rawMaterialDTO){
        return RawMaterial.builder()
                .code(rawMaterialDTO.code())
                .name(rawMaterialDTO.name())
                .stockQuantity(rawMaterialDTO.stockQuantity())
                .build();
    }

    public static RawMaterialResponseDTO toDTO(RawMaterial rawMaterial){
        return new RawMaterialResponseDTO(
                rawMaterial.getId(),
                rawMaterial.getCode(),
                rawMaterial.getName(),
                rawMaterial.getStockQuantity()
        );
    }
}
