package com.autoflex.autoflex.service.impl;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.mapper.ProductMapper;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.repository.ProductRepository;
import com.autoflex.autoflex.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponseDTO save(ProductDTO productDTO) {
        if(this.productRepository.existsByCode(productDTO.code())){
            throw new IllegalArgumentException("Product already exists");
        }

        Product productMapper = ProductMapper.toModel(productDTO);
        Product savedProduct = this.productRepository.saveAndFlush(productMapper);
        return ProductMapper.toResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO findById(UUID productId) {
        return this.productRepository.findById(productId)
                .map(ProductMapper::toResponseDTO)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return this.productRepository.findAll()
                .stream().map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getCode(),
                        product.getName(),
                        product.getPrice()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID productId) {
        Long rowsAffected = this.productRepository.deleteProductById(productId);

        if(rowsAffected == 0){
            throw new NotFoundException("Product not found");
        }

        this.productRepository.flush();
    }

    @Override
    @Transactional
    public ProductResponseDTO update(UUID productId, ProductDTO productDTO) {
        Product productSaved = this.productRepository.findById(productId).orElseThrow(
                () ->  new NotFoundException("Product not found"));

        boolean codeTaken = this.productRepository.existsByCode(productDTO.code());
        if(codeTaken){
            throw new IllegalArgumentException("Code already in use");
        }

        productSaved.setCode(productDTO.code());
        productSaved.setName(productDTO.name());
        productSaved.setPrice(productDTO.price());

        Product productUpdated = this.productRepository.saveAndFlush(productSaved);
        return ProductMapper.toResponseDTO(productUpdated);
    }
}
