package com.autoflex.autoflex.controller;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Raw Material Controller")
public interface RawMaterialController {

    @Operation(summary = "Create raw material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Raw material create successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RawMaterialDTO.class))})
    })
    ResponseEntity<RawMaterialResponseDTO> save(RawMaterialDTO rawMaterialDTO);

    @Operation(summary = "Get raw material by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get a raw material by ID",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RawMaterialDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Raw material not found",
                    content = @Content
            )
    })
    ResponseEntity<RawMaterialResponseDTO> findById(UUID rawMaterialId);

    @Operation(summary = "List all raw materials with pagination")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Raw materials retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RawMaterialResponseDTO.class)
                    )
            )
    })
    ResponseEntity<Page<RawMaterialResponseDTO>> findAll(Pageable pageable);
}
