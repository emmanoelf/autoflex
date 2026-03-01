package com.autoflex.autoflex.unit.service;

import com.autoflex.autoflex.dto.*;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.model.ProductRawMaterial;
import com.autoflex.autoflex.model.RawMaterial;
import com.autoflex.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.autoflex.repository.ProductRepository;
import com.autoflex.autoflex.repository.RawMaterialRepository;
import com.autoflex.autoflex.service.impl.ProductRawMaterialServiceImpl;
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
import org.springframework.data.domain.Sort;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
public class ProductRawMaterialServiceTest {

    @InjectMocks
    private ProductRawMaterialServiceImpl productRawMaterialService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductRawMaterialRepository productRawMaterialRepository;

    private ProductRawMaterial mockProductRawMaterial;
    private Product mockProduct;
    private RawMaterial mockRawMaterial;
    private RawMaterial mockAnotherRawMaterial;
    private static final UUID UUID_MOCK = UUID.fromString("932676cb-f277-4b99-93c9-837fdbe11b95");

    @BeforeEach
    public void setUp(){
        this.mockProduct = Product.builder()
                .id(UUID.fromString("e1b7ba2a-3851-4432-b7eb-5b517afc7a39"))
                .code("TAB6982")
                .name("Table")
                .price(new BigDecimal("84.32"))
                .rawMaterials(new ArrayList<>())
                .build();

        this.mockRawMaterial = RawMaterial.builder()
                .id(UUID.fromString("60dbf180-dfeb-4715-aa1f-11e56bb773a2"))
                .code("WOO4588")
                .name("Wood")
                .stockQuantity(300)
                .build();

        this.mockAnotherRawMaterial = RawMaterial.builder()
                .id(UUID.fromString("7cf1e60b-0dc0-4190-aabe-a64213c01109"))
                .code("NAI985")
                .name("Nail")
                .stockQuantity(1000)
                .build();

        this.mockProductRawMaterial = ProductRawMaterial.builder()
                .id(UUID.fromString("1112199a-5bf0-4fbe-b0d4-d0063a0b104a"))
                .product(this.mockProduct)
                .rawMaterial(this.mockRawMaterial)
                .requiredQuantity(12)
                .build();
    }

    @Test
    @DisplayName("Should associate raw materials with products successfully")
    public void shouldAssociateRawMaterialsWithProductsSuccessfully(){
        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(
                this.mockProduct.getId(),
                List.of(
                        new ProductRawMaterialInputDTO(this.mockRawMaterial.getId(), 10),
                        new ProductRawMaterialInputDTO(this.mockAnotherRawMaterial.getId(), 25)
                )
        );

        when(productRepository.findById(this.mockProduct.getId())).thenReturn(Optional.of(mockProduct));
        when(rawMaterialRepository.findById(this.mockRawMaterial.getId())).thenReturn(Optional.of(this.mockRawMaterial));
        when(rawMaterialRepository.findById(this.mockAnotherRawMaterial.getId())).thenReturn(Optional.of(this.mockAnotherRawMaterial));
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductRawMaterialResponseDTO response = this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO);

        verify(this.productRepository).findById(this.mockProduct.getId());
        verify(this.rawMaterialRepository).findById(this.mockRawMaterial.getId());
        verify(this.rawMaterialRepository).findById(this.mockAnotherRawMaterial.getId());
        verify(this.productRepository).saveAndFlush(this.mockProduct);

        assertEquals(this.mockProduct.getId(), response.id());
        assertEquals(2, response.rawMaterials().size());
    }

    @Test
    @DisplayName("Should throw NotFoundException if product does not exist")
    public void shouldThrowNotFoundWhenProductDoesNotExist() {
        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(this.mockProduct.getId(), List.of());

        when(productRepository.findById(this.mockProduct.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO));

        verify(this.productRepository).findById(this.mockProduct.getId());

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if raw material already associated")
    public void shouldThrowIllegalArgumentIfRawMaterialAlreadyAssociated() {
        this.mockProduct.getRawMaterials().add(mockProductRawMaterial);

        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(
                this.mockProduct.getId(),
                List.of(new ProductRawMaterialInputDTO(this.mockRawMaterial.getId(), 5))
        );

        when(this.productRepository.findById(mockProduct.getId())).thenReturn(Optional.of(this.mockProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO));

        assertEquals("One or more raw materials are already associated with this product", exception.getMessage());

        verify(this.productRepository).findById(this.mockProduct.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when raw material is not found")
    public void shouldThrowNotFoundExceptionWhenRawMaterialNotFound() {

        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(
                this.mockProduct.getId(),
                List.of(new ProductRawMaterialInputDTO(this.mockAnotherRawMaterial.getId(), 5))
        );

        when(this.productRepository.findById(this.mockProduct.getId()))
                .thenReturn(Optional.of(this.mockProduct));

        when(this.rawMaterialRepository.findById(this.mockAnotherRawMaterial.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> this.productRawMaterialService.associateProductsWithRawMaterials(inputDTO)
        );

        assertEquals("Raw material not found", exception.getMessage());

        verify(this.productRepository).findById(this.mockProduct.getId());
        verify(this.rawMaterialRepository).findById(this.mockAnotherRawMaterial.getId());
    }

    @Test
    @DisplayName("Should calculate suggested production correctly")
    public void shouldCalculateSuggestedProductionCorrectly(){
        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);

        when(this.productRepository.findAllProductsWithRawMaterialsOrderByPriceDesc()).thenReturn(List.of(this.mockProduct));

        List<ProductAvailableProductionDTO> response = this.productRawMaterialService.findProductsAvailableProduction();

        assertEquals(1, response.size());
        assertEquals(this.mockProduct.getId(), response.get(0).productId());
        assertEquals(25, response.get(0).quantityProductionAvailable());
    }

    @Test
    @DisplayName("Should ignore products without raw materials")
    public void shouldIgnoreProductsWithoutRawMaterials(){
        when(this.productRepository.findAllProductsWithRawMaterialsOrderByPriceDesc()).thenReturn(List.of(this.mockProduct));
        List<ProductAvailableProductionDTO> response = this.productRawMaterialService.findProductsAvailableProduction();

        verify(this.productRepository).findAllProductsWithRawMaterialsOrderByPriceDesc();

        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Should suggest product based on lowest limiting raw material")
    public void shouldSuggestProductBasedOnLowestLimitingRawMaterial(){
        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);
        ProductRawMaterial nailMaterial = ProductRawMaterial.builder()
                .id(UUID_MOCK)
                .product(this.mockProduct)
                .rawMaterial(this.mockAnotherRawMaterial)
                .requiredQuantity(100)
                .build();
        this.mockProduct.getRawMaterials().add(nailMaterial);

        when(this.productRepository.findAllProductsWithRawMaterialsOrderByPriceDesc()).thenReturn(List.of(this.mockProduct));

        List<ProductAvailableProductionDTO> response = this.productRawMaterialService.findProductsAvailableProduction();

        assertEquals(1, response.size());
        assertEquals(10, response.get(0).quantityProductionAvailable());
    }

    @Test
    @DisplayName("Should use stock in memory and affect all production")
    public void shouldUseStockInMemoryAndAffectAllProduction(){
        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);
        Product chairProduct = Product.builder()
                .id(UUID.randomUUID())
                .code("CHA153")
                .name("Chair")
                .price(new BigDecimal("33.89"))
                .rawMaterials(new ArrayList<>())
                .build();

        ProductRawMaterial chairAssociation = ProductRawMaterial.builder()
                .id(UUID.randomUUID())
                .product(chairProduct)
                .rawMaterial(this.mockRawMaterial)
                .requiredQuantity(2)
                .build();
        this.mockProduct.getRawMaterials().add(chairAssociation);

        when(this.productRepository.findAllProductsWithRawMaterialsOrderByPriceDesc()).thenReturn(List.of(this.mockProduct));

        List<ProductAvailableProductionDTO> response = this.productRawMaterialService.findProductsAvailableProduction();

        assertEquals(1, response.size());
        assertEquals(mockProduct.getId(), response.get(0).productId());
        assertEquals(25, response.get(0).quantityProductionAvailable());
    }

    @Test
    @DisplayName("Should be able to delete association by id successfully")
    public void shouldBeAbleToDeleteRawMaterialByIdSuccessfully(){
        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);

        when(this.productRawMaterialRepository.findById(this.mockProductRawMaterial.getId()))
                .thenReturn(Optional.of(this.mockProductRawMaterial));

        this.productRawMaterialService.deleteById(this.mockProductRawMaterial.getId());

        verify(this.productRawMaterialRepository).findById(this.mockProductRawMaterial.getId());

        assertFalse(this.mockProduct.getRawMaterials().contains(this.mockProductRawMaterial));
    }

    @Test
    @DisplayName("Should be throw exception if association not found")
    public void shouldBeThrowExceptionWhenRawMaterialNotExists(){
        when(this.productRawMaterialRepository.findById(UUID_MOCK)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productRawMaterialService.deleteById(UUID_MOCK));

        verify(this.productRawMaterialRepository).findById(UUID_MOCK);

        assertEquals("Association not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return paginated products with materials")
    void shouldReturnPaginatedProductsWithMaterials() {
        int page = 0;
        int size = 10;
        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);

        Page<Product> productPage = new PageImpl<>(List.of(this.mockProduct));
        when(this.productRepository.findAll(PageRequest.of(page, size, Sort.by("name").ascending())))
                .thenReturn(productPage);

        PageResponseDTO<ProductRawMaterialFindAllDTO> response =
                this.productRawMaterialService.findAllProductsWithMaterials(page, size);

        verify(this.productRepository).findAll(PageRequest.of(page, size, Sort.by("name").ascending()));

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(this.mockProduct.getId(), response.content().getFirst().id());
        assertFalse(response.content().getFirst().rawMaterials().isEmpty());
    }

    @Test
    @DisplayName("Should return product raw material by ID")
    void shouldReturnProductById() {
        when(this.productRepository.findById(this.mockProduct.getId())).thenReturn(Optional.of(this.mockProduct));

        ProductRawMaterialResponseDTO response = this.productRawMaterialService.findByProductId(this.mockProduct.getId());

        verify(this.productRepository).findById(this.mockProduct.getId());

        assertNotNull(response);
        assertEquals(this.mockProduct.getId(), response.id());
    }

    @Test
    @DisplayName("Should throw exception when product ID does not exist")
    void shouldThrowNotFoundWhenProductIdNotFound() {
        when(this.productRepository.findById(UUID_MOCK)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productRawMaterialService.findByProductId(UUID_MOCK));

        assertEquals("Association not found", exception.getMessage());
        verify(this.productRepository).findById(UUID_MOCK);
    }

    @Test
    @DisplayName("Should update product raw material successfully")
    void shouldUpdateProductRawMaterialSuccessfully() {
        ProductRawMaterialInputDTO inputDTO = new ProductRawMaterialInputDTO(
                this.mockAnotherRawMaterial.getId(), 5
        );

        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);

        when(this.productRawMaterialRepository.findById(this.mockProductRawMaterial.getId()))
                .thenReturn(Optional.of(this.mockProductRawMaterial));
        when(this.rawMaterialRepository.findById(this.mockAnotherRawMaterial.getId()))
                .thenReturn(Optional.of(this.mockAnotherRawMaterial));
        when(this.productRawMaterialRepository.saveAndFlush(this.mockProductRawMaterial))
                .thenAnswer(inv -> inv.getArgument(0));

        ProductRawMaterialResponseDTO response = this.productRawMaterialService
                .update(this.mockProductRawMaterial.getId(), inputDTO);

        verify(this.productRawMaterialRepository).findById(this.mockProductRawMaterial.getId());
        verify(this.rawMaterialRepository).findById(this.mockAnotherRawMaterial.getId());
        verify(this.productRawMaterialRepository).saveAndFlush(this.mockProductRawMaterial);

        assertNotNull(response);
        assertEquals(this.mockProduct.getId(), response.id());
        assertEquals(1, response.rawMaterials().size());
        assertEquals(this.mockAnotherRawMaterial.getId(), response.rawMaterials().get(0).rawMaterialId());
        assertEquals(5, response.rawMaterials().get(0).requiredQuantity());
    }

    @Test
    @DisplayName("Should throw NotFoundException if association not found")
    void shouldThrowNotFoundIfAssociationNotFound() {
        ProductRawMaterialInputDTO inputDTO = new ProductRawMaterialInputDTO(this.mockRawMaterial.getId(), 5);

        when(this.productRawMaterialRepository.findById(UUID_MOCK)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productRawMaterialService.update(UUID_MOCK, inputDTO));

        assertEquals("Association not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException if raw material not found")
    void shouldThrowNotFoundIfRawMaterialNotFound() {
        ProductRawMaterialInputDTO inputDTO = new ProductRawMaterialInputDTO(UUID.randomUUID(), 5);

        this.mockProduct.getRawMaterials().add(this.mockProductRawMaterial);

        when(this.productRawMaterialRepository.findById(this.mockProductRawMaterial.getId()))
                .thenReturn(Optional.of(this.mockProductRawMaterial));
        when(this.rawMaterialRepository.findById(inputDTO.rawMaterialId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productRawMaterialService.update(this.mockProductRawMaterial.getId(), inputDTO));

        assertEquals("Raw material not found", exception.getMessage());
    }

}
