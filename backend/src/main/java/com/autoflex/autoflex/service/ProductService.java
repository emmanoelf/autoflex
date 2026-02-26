package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO save(ProductDTO productDTO);
    ProductResponseDTO findById(UUID productId);
    List<ProductResponseDTO> findAll();
    void deleteById(UUID productId);
    ProductResponseDTO update(UUID productId, ProductDTO productDTO);
}
