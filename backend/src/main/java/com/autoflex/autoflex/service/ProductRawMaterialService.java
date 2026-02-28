package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.*;

import java.util.List;
import java.util.UUID;

public interface ProductRawMaterialService {
    ProductRawMaterialResponseDTO associateProductsWithRawMaterials(ProductWithMaterialInputDTO inputDTO);
    List<ProductAvailableProductionDTO> findProductsAvailableProduction();
    void deleteById(UUID associationId);
    PageResponseDTO<ProductRawMaterialFindAllDTO> findAllProductsWithMaterials(int page, int size);
    ProductRawMaterialResponseDTO findByProductId(UUID productRawMaterialId);
    ProductRawMaterialResponseDTO update(UUID productRawMaterialId, ProductRawMaterialInputDTO inputDTO);

}
