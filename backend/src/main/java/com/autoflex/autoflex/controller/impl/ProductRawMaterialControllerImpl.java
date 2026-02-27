package com.autoflex.autoflex.controller.impl;

import com.autoflex.autoflex.controller.ProductRawMaterialController;
import com.autoflex.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.autoflex.dto.ProductWithMaterialInputDTO;
import com.autoflex.autoflex.service.ProductRawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/associate-products-raw-materials")
@RequiredArgsConstructor
public class ProductRawMaterialControllerImpl implements ProductRawMaterialController {
    private final ProductRawMaterialService productRawMaterialService;

    @Override
    @PostMapping
    public ResponseEntity<ProductRawMaterialResponseDTO> associateProductWithRawMaterials(@RequestBody ProductWithMaterialInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO));
    }
}
