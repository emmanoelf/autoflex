package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.PageResponseDTO;
import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RawMaterialService {
    RawMaterialResponseDTO save(RawMaterialDTO rawMaterialDTO);
    RawMaterialResponseDTO findById(UUID rawMaterialID);
    PageResponseDTO<RawMaterialResponseDTO> findAll(Pageable pageable);
    void deleteById(UUID rawMaterialId);
    RawMaterialResponseDTO update(UUID rawMaterialId, RawMaterialDTO rawMaterialDTO);
}
