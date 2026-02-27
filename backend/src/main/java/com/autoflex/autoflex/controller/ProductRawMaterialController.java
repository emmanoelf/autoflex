package com.autoflex.autoflex.controller;

import com.autoflex.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.autoflex.dto.ProductWithMaterialInputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

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
}
