package com.autoflex.autoflex.controller;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Product Controller")
public interface ProductController {

    @Operation(summary = "Create product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Create product",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))})
    })
    ResponseEntity<ProductResponseDTO> save(ProductDTO productDTO);

    @Operation(summary = "Get a product by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get a product by ID",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    ResponseEntity<ProductResponseDTO> findById(UUID productId);

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all products",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class))
                    )
            )
    })
    ResponseEntity<List<ProductResponseDTO>> findAll();

    @Operation(summary = "Delete product by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    ResponseEntity<List<ProductResponseDTO>> deleteById(UUID productId);

    @Operation(summary = "Update product by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or code already exists",
                    content = @Content
            )
    })
    ResponseEntity<ProductResponseDTO> update(UUID productId, ProductDTO productDTO);
}
