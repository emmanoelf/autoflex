package com.autoflex.autoflex.mapper;

import com.autoflex.autoflex.dto.ProductRawMaterialDTO;
import com.autoflex.autoflex.dto.ProductRawMaterialInputDTO;
import com.autoflex.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.model.ProductRawMaterial;
import com.autoflex.autoflex.model.RawMaterial;

import java.util.List;

public class ProductRawMaterialMapper {

    public static ProductRawMaterial toModel(Product product, RawMaterial rawMaterial, ProductRawMaterialInputDTO dto){
        return ProductRawMaterial.builder()
                .product(product)
                .rawMaterial(rawMaterial)
                .requiredQuantity(dto.requiredQuantity())
                .build();
    }

    public static ProductRawMaterialDTO toDTO(ProductRawMaterial productRawMaterial){
        return new ProductRawMaterialDTO(
                productRawMaterial.getId(),
                productRawMaterial.getRawMaterial().getName(),
                productRawMaterial.getRequiredQuantity()
        );
    }

    public static ProductRawMaterialResponseDTO toResponseDTO(Product product){
        List<ProductRawMaterialDTO> rawMaterialsDto = product.getRawMaterials()
                .stream().map(ProductRawMaterialMapper::toDTO)
                .toList();

        return new ProductRawMaterialResponseDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                rawMaterialsDto
        );
    }
}
