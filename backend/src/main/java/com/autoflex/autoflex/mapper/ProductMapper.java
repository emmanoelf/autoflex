package com.autoflex.autoflex.mapper;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import com.autoflex.autoflex.model.Product;

public class ProductMapper {

    public static Product toModel(ProductDTO productDTO){
        return Product.builder()
                .code(productDTO.code())
                .name(productDTO.name())
                .price(productDTO.price())
                .build();
    }

    public static ProductResponseDTO toResponseDTO(Product product){
        return new ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice()
        );
    }
}
