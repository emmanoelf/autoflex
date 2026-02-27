package com.autoflex.autoflex.mapper;

import com.autoflex.autoflex.dto.ProductAvailableProductionDTO;
import com.autoflex.autoflex.model.Product;

import java.math.BigDecimal;

public class ProductAvailableProductionMapper {
    public static ProductAvailableProductionDTO toDTO(Product product, int maxProductionQuantity){
        return new ProductAvailableProductionDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                maxProductionQuantity,
                product.getPrice().multiply(BigDecimal.valueOf(maxProductionQuantity))
        );
    }
}
