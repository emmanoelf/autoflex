package com.autoflex.autoflex.mapper;

import com.autoflex.autoflex.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class PageMapper {
    public static <T> PageResponseDTO<T> toPageResponseDTO(Page<T> page) {
        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
