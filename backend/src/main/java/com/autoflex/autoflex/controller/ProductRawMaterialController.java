package com.autoflex.autoflex.controller;

import com.autoflex.autoflex.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ProductRawMaterialController {

    @Operation(summary = "Associate product with raw materials",
            description = "Associates one product with multiple raw materials and their required quantities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Product successfully associated with raw materials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductRawMaterialResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product or raw material not found",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content)
    })
    ResponseEntity<ProductRawMaterialResponseDTO> associateProductWithRawMaterials(ProductWithMaterialInputDTO inputDTO);

    @Operation(
            summary = "Get producible products with quantities and total value",
            description = "Returns a list of products that can be produced with the available raw materials in stock. " +
                    "The list is ordered by product total value in descending order. " +
                    "Each product includes the maximum producible quantity and the total value."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of producible products retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductAvailableProductionDTO.class)))),
            @ApiResponse(responseCode = "204",
                    description = "No products can be produced with the available stock",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    ResponseEntity<List<ProductAvailableProductionDTO>> findProductsAvailableProduction();

    @Operation(
            summary = "Remove raw material from product",
            description = "Removes the association between a product and a raw material."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Association successfully removed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association not found",
                    content = @Content
            )
    })
    ResponseEntity<Void> deleteAssociationById(UUID productRawMaterialId);

    @Operation(summary = "List all products with their raw materials (paginated)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products and raw materials retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductRawMaterialFindAllDTO.class)
                    )
            )
    })
    Page<ProductRawMaterialFindAllDTO> findAllProductsWithMaterials(int page, int size);

    @Operation(summary = "Get a product raw material association by product id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Association retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductRawMaterialResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association not found"
            )
    })
    ResponseEntity<ProductRawMaterialResponseDTO> findByProductId(UUID productRawMaterialId);

    @Operation(summary = "Update a product-raw-material association")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Association updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductRawMaterialResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Association not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            )
    })
    ResponseEntity<ProductRawMaterialResponseDTO> update(UUID productRawMaterialId, ProductRawMaterialInputDTO inputDTO);
}
