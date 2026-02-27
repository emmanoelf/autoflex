package com.autoflex.autoflex.controller.impl;

import com.autoflex.autoflex.controller.ProductController;
import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import com.autoflex.autoflex.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {
    private final ProductService productService;

    @Override
    @PostMapping
    public ResponseEntity<ProductResponseDTO> save(@RequestBody @Valid ProductDTO productDTO) {
        ProductResponseDTO productSaved = this.productService.save(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productSaved);
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable UUID productId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.findById(productId));
    }

    @Override
    @GetMapping("/")
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.findAll());
    }

    @Override
    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> deleteById(@PathVariable UUID productId) {
        this.productService.deleteById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable UUID productId, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.update(productId, productDTO));
    }
}
