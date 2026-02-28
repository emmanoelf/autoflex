package com.autoflex.autoflex.controller.impl;

import com.autoflex.autoflex.controller.ProductRawMaterialController;
import com.autoflex.autoflex.dto.*;
import com.autoflex.autoflex.service.ProductRawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products-raw-materials")
@RequiredArgsConstructor
public class ProductRawMaterialControllerImpl implements ProductRawMaterialController {
    private final ProductRawMaterialService productRawMaterialService;

    @Override
    @PostMapping("/associate")
    public ResponseEntity<ProductRawMaterialResponseDTO> associateProductWithRawMaterials(@RequestBody ProductWithMaterialInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO));
    }

    @Override
    @GetMapping("/suggested-production-available")
    public ResponseEntity<List<ProductAvailableProductionDTO>> findProductsAvailableProduction() {
        return ResponseEntity.status(HttpStatus.OK).body(this.productRawMaterialService.findProductsAvailableProduction());
    }

    @Override
    @DeleteMapping("/{productRawMaterialId}")
    public ResponseEntity<Void> deleteAssociationById(@PathVariable UUID productRawMaterialId) {
        this.productRawMaterialService.deleteById(productRawMaterialId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/")
    public Page<ProductRawMaterialFindAllDTO> findAllProductsWithMaterials(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        return this.productRawMaterialService.findAllProductsWithMaterials(page, size);
    }

    @Override
    @GetMapping("/{productRawMaterialId}")
    public ResponseEntity<ProductRawMaterialResponseDTO> findByProductId(@PathVariable UUID productRawMaterialId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productRawMaterialService.findByProductId(productRawMaterialId));
    }

    @Override
    @PutMapping("/{productRawMaterialId}")
    public ResponseEntity<ProductRawMaterialResponseDTO> update(@PathVariable UUID productRawMaterialId,
                                                                @RequestBody ProductRawMaterialInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productRawMaterialService.update(productRawMaterialId, inputDTO));
    }
}
