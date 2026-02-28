package com.autoflex.autoflex.service.impl;

import com.autoflex.autoflex.dto.*;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.mapper.ProductAvailableProductionMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

        Set<UUID> existingRawMaterialIds = product.getRawMaterials()
                .stream().map(rm -> rm.getRawMaterial().getId()).collect(Collectors.toSet());

        boolean idAlreadyExists = inputDTO.rawMaterials().stream()
                .anyMatch(rm -> existingRawMaterialIds.contains(rm.rawMaterialId()));

        if (idAlreadyExists) {
            throw new IllegalArgumentException("One or more raw materials are already associated with this product");
        }

        List<ProductRawMaterial> rawMaterialAssociations = inputDTO.rawMaterials().stream().map(rm -> {
            RawMaterial rawMaterial = this.rawMaterialRepository.findById(rm.rawMaterialId()).orElseThrow(
                    () -> new NotFoundException("Raw material not found"));

            return ProductRawMaterialMapper.toModel(product, rawMaterial, rm);
        }).toList();

        product.getRawMaterials().addAll(rawMaterialAssociations);
        this.productRepository.saveAndFlush(product);

        return ProductRawMaterialMapper.toResponseDTO(product);
    }

    @Override
    public List<ProductAvailableProductionDTO> findProductsAvailableProduction() {
        List<Product> products = this.productRepository.findAllProductsWithRawMaterialsOrderByPriceDesc();
        List<ProductAvailableProductionDTO> suggestedProduction = new ArrayList<>();

        Map<UUID, Integer> stockInMemory = this.createGlobalStockInMemory(products);

        for (Product product : products) {
            if (product.getRawMaterials().isEmpty()){
                continue;
            }

            int maxProductionQuantity = this.calculateMaxProduction(product, stockInMemory);

            if (maxProductionQuantity > 0) {
                suggestedProduction.add(ProductAvailableProductionMapper.toDTO(product, maxProductionQuantity));
                this.consumeStockInMemory(product, stockInMemory, maxProductionQuantity);
            }
        }

        return suggestedProduction;
    }

    private Map<UUID, Integer> createGlobalStockInMemory(List<Product> products) {
        Map<UUID, Integer> stockMap = new HashMap<>();
        for (Product product : products) {
            for (ProductRawMaterial prm : product.getRawMaterials()) {
                UUID rawId = prm.getRawMaterial().getId();
                if (!stockMap.containsKey(rawId)) {
                    stockMap.put(rawId, prm.getRawMaterial().getStockQuantity());
                }
            }
        }
        return stockMap;
    }

    private int calculateMaxProduction(Product product, Map<UUID, Integer> stockInMemory) {
        return product.getRawMaterials()
                .stream()
                .mapToInt(prm -> stockInMemory.get(prm.getRawMaterial().getId()) / prm.getRequiredQuantity())
                .min()
                .orElse(0);
    }

    private void consumeStockInMemory(Product product, Map<UUID, Integer> stockInMemory, int quantityProduced) {
        for (ProductRawMaterial prm : product.getRawMaterials()) {
            UUID rawId = prm.getRawMaterial().getId();
            int updatedStock = stockInMemory.get(rawId) - (quantityProduced * prm.getRequiredQuantity());
            stockInMemory.put(rawId, updatedStock);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID associationId) {
        ProductRawMaterial productRawMaterial = this.productRawMaterialRepository.findById(associationId)
                .orElseThrow(() -> new NotFoundException("Association not found"));

        Product product = productRawMaterial.getProduct();
        product.getRawMaterials().remove(productRawMaterial);
    }

    @Override
    public Page<ProductRawMaterialFindAllDTO> findAllProductsWithMaterials(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> productsPage = this.productRepository.findAll(pageable);

        return productsPage.map(product -> ProductRawMaterialMapper.toResponseDTO(product, true));
    }

    @Override
    public ProductRawMaterialResponseDTO findByProductId(UUID productRawMaterialId) {
        Product product = this.productRepository.findById(productRawMaterialId)
                .orElseThrow(() -> new NotFoundException("Association not found"));

        return ProductRawMaterialMapper.toResponseDTO(product);
    }

    @Override
    public ProductRawMaterialResponseDTO update(UUID productRawMaterialId, ProductRawMaterialInputDTO inputDTO) {
        ProductRawMaterial productRawMaterial = this.productRawMaterialRepository.findById(productRawMaterialId)
                .orElseThrow(() -> new NotFoundException("Association not found"));

        RawMaterial newRawMaterial = this.rawMaterialRepository.findById(inputDTO.rawMaterialId())
                .orElseThrow(() -> new NotFoundException("Raw material not found"));

        boolean exists = productRawMaterial.getProduct().getRawMaterials().stream()
                .anyMatch(prm -> prm.getRawMaterial().getId().equals(inputDTO.rawMaterialId())
                        && !prm.getId().equals(productRawMaterialId));

        if (exists) {
            throw new IllegalArgumentException("This product already has the specified raw material associated");
        }

        productRawMaterial.setRawMaterial(newRawMaterial);
        productRawMaterial.setRequiredQuantity(inputDTO.requiredQuantity());

        productRawMaterialRepository.saveAndFlush(productRawMaterial);

        return ProductRawMaterialMapper.toResponseDTO(productRawMaterial.getProduct());
    }
}
