package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.ProductAvailableProductionDTO;
import com.autoflex.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.autoflex.dto.ProductWithMaterialInputDTO;

import java.util.List;

public interface ProductRawMaterialService {
    ProductRawMaterialResponseDTO associateProductsWithRawMaterials(ProductWithMaterialInputDTO inputDTO);
    List<ProductAvailableProductionDTO> findProductsAvailableProduction();
}
