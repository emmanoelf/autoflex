package com.autoflex.autoflex.service;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;

public interface RawMaterialService {
    RawMaterialResponseDTO save(RawMaterialDTO rawMaterialDTO);
}
