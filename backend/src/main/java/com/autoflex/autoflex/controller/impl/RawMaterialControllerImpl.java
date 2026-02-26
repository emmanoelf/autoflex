package com.autoflex.autoflex.controller.impl;

import com.autoflex.autoflex.controller.RawMaterialController;
import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.autoflex.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/raw-materials")
@RequiredArgsConstructor
public class RawMaterialControllerImpl implements RawMaterialController {
    private final RawMaterialService rawMaterialService;

    @Override
    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> save(@RequestBody @Valid RawMaterialDTO rawMaterialDTO) {
        RawMaterialResponseDTO rawMaterialSaved = this.rawMaterialService.save(rawMaterialDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialSaved);
    }
}
