package com.autoflex.autoflex.unit.service;

import com.autoflex.autoflex.dto.*;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.model.RawMaterial;
import com.autoflex.autoflex.repository.RawMaterialRepository;
import com.autoflex.autoflex.service.impl.RawMaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
public class RawMaterialServiceTest {

    @InjectMocks
    private RawMaterialServiceImpl rawMaterialService;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    private RawMaterial mockRawMaterial;
    private static final UUID UUID_MOCK = UUID.fromString("dfe15fb2-8f10-4b76-ae72-eb5d3c254326");

    @BeforeEach
    public void setUp() {
        this.mockRawMaterial = RawMaterial.builder()
                .id(UUID.fromString("60dbf180-dfeb-4715-aa1f-11e56bb773a2"))
                .code("WOO4588")
                .name("Wood")
                .stockQuantity(300)
                .build();
    }

    @Test
    @DisplayName("Should be able to create raw material successfully")
    public void shouldBeAbleToCreateRawMaterialSuccessfully() {
        RawMaterialDTO rawMaterialDTO = new RawMaterialDTO("WOO4588", "Wood", 300);

        when(this.rawMaterialRepository.existsByCode(rawMaterialDTO.code())).thenReturn(false);
        when(this.rawMaterialRepository.saveAndFlush(any(RawMaterial.class))).thenReturn(this.mockRawMaterial);

        RawMaterialResponseDTO response = this.rawMaterialService.save(rawMaterialDTO);

        verify(this.rawMaterialRepository).existsByCode(rawMaterialDTO.code());
        verify(this.rawMaterialRepository).saveAndFlush(any(RawMaterial.class));

        assertNotNull(response);
        assertEquals(this.mockRawMaterial.getId(), response.id());
        assertEquals(this.mockRawMaterial.getCode(), response.code());
        assertEquals(this.mockRawMaterial.getName(), response.name());
        assertEquals(this.mockRawMaterial.getStockQuantity(), response.stockQuantity());
    }

    @Test
    @DisplayName("Should throw exception when code already exists")
    public void shouldThrowExceptionWhenCodeAlreadyExists() {
        RawMaterialDTO rawMaterialDTO = new RawMaterialDTO("WOO4588", "Wood", 300);

        when(this.rawMaterialRepository.existsByCode(rawMaterialDTO.code())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.rawMaterialService.save(rawMaterialDTO));

        assertEquals("Code already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to get raw material by id")
    public void shouldBeAbleToGetRawMaterialById(){
        when(this.rawMaterialRepository.findById(this.mockRawMaterial.getId()))
                .thenReturn(Optional.of(this.mockRawMaterial));

        RawMaterialResponseDTO responseDTO = this.rawMaterialService.findById(this.mockRawMaterial.getId());

        assertEquals(responseDTO.id(), this.mockRawMaterial.getId());
        assertAll(
                () -> assertEquals(this.mockRawMaterial.getId(), responseDTO.id()),
                () -> assertEquals(this.mockRawMaterial.getCode(), responseDTO.code()),
                () -> assertEquals(this.mockRawMaterial.getName(), responseDTO.name()),
                () -> assertEquals(this.mockRawMaterial.getStockQuantity(), responseDTO.stockQuantity())
        );
    }

    @Test
    @DisplayName("Should throw exception when raw material not found")
    public void shouldThrowExceptionWhenRawMaterialNotFound(){
        when(this.rawMaterialRepository.findById(UUID_MOCK)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.rawMaterialService.findById(UUID_MOCK));

        assertEquals("Raw material not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to get all raw materials paged")
    void shouldBeAbleToGetAllRawMaterialsPaged() {
        Pageable pageable = PageRequest.of(0, 10);

        RawMaterial anotherRawMaterial = RawMaterial.builder()
                .id(UUID.randomUUID())
                .code("MET123")
                .name("Metal")
                .stockQuantity(150)
                .build();
        List<RawMaterial> rawMaterials = List.of(this.mockRawMaterial, anotherRawMaterial);
        Page<RawMaterial> pageMock = new PageImpl<>(rawMaterials, pageable, rawMaterials.size());

        when(this.rawMaterialRepository.findAll(pageable)).thenReturn(pageMock);

        PageResponseDTO<RawMaterialResponseDTO> response = this.rawMaterialService.findAll(pageable);

        verify(this.rawMaterialRepository).findAll(pageable);

        assertEquals(rawMaterials.size(), response.content().size());

        for (int i = 0; i < rawMaterials.size(); i++) {
            RawMaterial rm = rawMaterials.get(i);
            RawMaterialResponseDTO dto = response.content().get(i);

            assertAll(
                    () -> assertEquals(rm.getId(), dto.id()),
                    () -> assertEquals(rm.getCode(), dto.code()),
                    () -> assertEquals(rm.getName(), dto.name()),
                    () -> assertEquals(rm.getStockQuantity(), dto.stockQuantity())
            );
        }

        assertEquals(pageable.getPageNumber(), response.pageNumber());
        assertEquals(pageable.getPageSize(), response.pageSize());
        assertEquals(rawMaterials.size(), response.totalElements());
    }

    @Test
    @DisplayName("Should be able to delete rawMaterial by id successfully")
    public void shouldBeAbleToDeleteRawMaterialByIdSuccessfully(){
        when(this.rawMaterialRepository.deleteRawMaterialById(this.mockRawMaterial.getId())).thenReturn(1L);

        this.rawMaterialService.deleteById(this.mockRawMaterial.getId());

        verify(this.rawMaterialRepository).deleteRawMaterialById(this.mockRawMaterial.getId());
        verify(this.rawMaterialRepository).flush();
    }

    @Test
    @DisplayName("Should be throw exception when raw material not exists")
    public void shouldBeThrowExceptionWhenRawMaterialNotExists(){
        when(this.rawMaterialRepository.deleteRawMaterialById(UUID_MOCK)).thenReturn(0L);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.rawMaterialService.deleteById(UUID_MOCK));

        assertEquals("Raw material not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to update raw material successfully")
    public void shouldBeAbleToUpdateRawMaterialSuccessfully(){
        RawMaterialDTO rawMaterialUpdated = new RawMaterialDTO("MIR4513", "Mirror", 560);

        when(this.rawMaterialRepository.findById(this.mockRawMaterial.getId())).thenReturn(Optional.of(this.mockRawMaterial));
        when(this.rawMaterialRepository.existsByCodeAndIdNot(
                rawMaterialUpdated.code(), this.mockRawMaterial.getId())).thenReturn(false);
        when(this.rawMaterialRepository.saveAndFlush(any(RawMaterial.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RawMaterialResponseDTO response = this.rawMaterialService.update(this.mockRawMaterial.getId(), rawMaterialUpdated);

        verify(this.rawMaterialRepository).findById(this.mockRawMaterial.getId());
        verify(this.rawMaterialRepository).existsByCodeAndIdNot(rawMaterialUpdated.code(), this.mockRawMaterial.getId());
        verify(this.rawMaterialRepository).saveAndFlush(any(RawMaterial.class));

        assertEquals(this.mockRawMaterial.getId(), response.id());
        assertEquals(rawMaterialUpdated.code(), response.code());
        assertEquals(rawMaterialUpdated.name(), response.name());
        assertEquals(rawMaterialUpdated.stockQuantity(), response.stockQuantity());
    }

    @Test
    @DisplayName("Should throw exception when code already in use")
    public void shouldThrowExceptionWhenCodeAlreadyInUse(){
        RawMaterialDTO rawMaterialUpdated = new RawMaterialDTO("MIR4513", "Mirror", 560);
        when(this.rawMaterialRepository.findById(this.mockRawMaterial.getId())).thenReturn(Optional.of(this.mockRawMaterial));
        when(this.rawMaterialRepository.existsByCodeAndIdNot(
                rawMaterialUpdated.code(), this.mockRawMaterial.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.rawMaterialService.update(this.mockRawMaterial.getId(), rawMaterialUpdated));

        verify(this.rawMaterialRepository).findById(this.mockRawMaterial.getId());
        verify(this.rawMaterialRepository).existsByCodeAndIdNot(rawMaterialUpdated.code(), this.mockRawMaterial.getId());

        assertEquals("Code already in use", exception.getMessage());
    }

}
