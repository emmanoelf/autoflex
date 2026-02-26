package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;

import java.util.UUID;

public interface RawMaterialService {
    RawMaterialResponseDTO save(RawMaterialDTO rawMaterialDTO);
    RawMaterialResponseDTO findById(UUID rawMaterialID);
}
