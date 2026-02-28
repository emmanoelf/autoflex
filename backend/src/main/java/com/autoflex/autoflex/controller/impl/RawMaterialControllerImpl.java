package com.autoflex.autoflex.controller.impl;

import com.autoflex.autoflex.controller.RawMaterialController;
import com.autoflex.autoflex.dto.PageResponseDTO;
import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.autoflex.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @Override
    @GetMapping("/{rawMaterialId}")
    public ResponseEntity<RawMaterialResponseDTO> findById(@PathVariable UUID rawMaterialId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.rawMaterialService.findById(rawMaterialId));
    }

    @Override
    @GetMapping("/")
    public ResponseEntity<PageResponseDTO<RawMaterialResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.rawMaterialService.findAll(pageable));
    }

    @Override
    @DeleteMapping("/{rawMaterialId}")
    public ResponseEntity<RawMaterialResponseDTO> deleteById(@PathVariable UUID rawMaterialId) {
        this.rawMaterialService.deleteById(rawMaterialId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PutMapping("/{rawMaterialId}")
    public ResponseEntity<RawMaterialResponseDTO> update(@PathVariable UUID rawMaterialId,
                                                         @RequestBody RawMaterialDTO rawMaterialDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(this.rawMaterialService.update(rawMaterialId, rawMaterialDTO));
    }
}
