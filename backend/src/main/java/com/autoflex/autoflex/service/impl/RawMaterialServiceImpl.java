package com.autoflex.autoflex.service.impl;

import com.autoflex.autoflex.dto.RawMaterialDTO;
import com.autoflex.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.mapper.RawMaterialMapper;
import com.autoflex.autoflex.model.RawMaterial;
import com.autoflex.autoflex.repository.RawMaterialRepository;
import com.autoflex.autoflex.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements RawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    public RawMaterialResponseDTO save(RawMaterialDTO rawMaterialDTO) {
        if(this.rawMaterialRepository.existsByCode(rawMaterialDTO.code())){
            throw new IllegalArgumentException("Code already exists");
        }

        RawMaterial rawMaterial = RawMaterialMapper.toModel(rawMaterialDTO);
        RawMaterial savedRawMaterial = this.rawMaterialRepository.saveAndFlush(rawMaterial);
        return RawMaterialMapper.toDTO(savedRawMaterial);
    }

    @Override
    public RawMaterialResponseDTO findById(UUID rawMaterialID) {
        return this.rawMaterialRepository.findById(rawMaterialID)
                .map(RawMaterialMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Raw material not found"));
    }

    @Override
    public Page<RawMaterialResponseDTO> findAll(Pageable pageable) {
        return this.rawMaterialRepository.findAll(pageable)
                .map(rawMaterial -> new RawMaterialResponseDTO(
                        rawMaterial.getId(),
                        rawMaterial.getCode(),
                        rawMaterial.getName(),
                        rawMaterial.getStockQuantity()
                ));
    }
}
