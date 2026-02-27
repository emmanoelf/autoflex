package com.autoflex.autoflex.service.impl;

import com.autoflex.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.autoflex.dto.ProductWithMaterialInputDTO;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.mapper.ProductRawMaterialMapper;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.model.ProductRawMaterial;
import com.autoflex.autoflex.model.RawMaterial;
import com.autoflex.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.autoflex.repository.ProductRepository;
import com.autoflex.autoflex.repository.RawMaterialRepository;
import com.autoflex.autoflex.service.ProductRawMaterialService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRawMaterialServiceImpl implements ProductRawMaterialService {
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductRawMaterialRepository productRawMaterialRepository;

    @Override
    @Transactional
    public ProductRawMaterialResponseDTO associateProductsWithRawMaterials(ProductWithMaterialInputDTO inputDTO) {
        Product product = this.productRepository.findById(inputDTO.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<ProductRawMaterial> rawMaterialAssociations = inputDTO.rawMaterials().stream().map(rm -> {
            RawMaterial rawMaterial = this.rawMaterialRepository.findById(rm.rawMaterialId()).orElseThrow(
                    () -> new NotFoundException("Raw material not found"));

            return ProductRawMaterialMapper.toModel(product, rawMaterial, rm);
        }).toList();

        product.setRawMaterials(rawMaterialAssociations);
        this.productRawMaterialRepository.saveAllAndFlush(rawMaterialAssociations);

        return ProductRawMaterialMapper.toResponseDTO(product);
    }
}
