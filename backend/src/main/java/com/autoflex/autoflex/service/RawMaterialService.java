package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RawMaterialService {
    RawMaterialResponseDTO save(RawMaterialDTO rawMaterialDTO);
    RawMaterialResponseDTO findById(UUID rawMaterialID);
    Page<RawMaterialResponseDTO> findAll(Pageable pageable);
}
