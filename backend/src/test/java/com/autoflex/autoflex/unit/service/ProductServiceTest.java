package com.autoflex.autoflex.unit.service;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import com.autoflex.autoflex.exception.NotFoundException;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.repository.ProductRepository;
import com.autoflex.autoflex.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    private Product mockProduct;
    private static final UUID UUID_MOCK = UUID.fromString("143e0c2f-e1ee-4f83-8170-e73382fe4e46");

    @BeforeEach
    public void setUp(){
        this.mockProduct = Product.builder()
                .id(UUID.fromString("e1b7ba2a-3851-4432-b7eb-5b517afc7a39"))
                .code("TAB6982")
                .name("Table")
                .price(new BigDecimal("84.32"))
                .build();
    }

    @Test
    @DisplayName("Should be able to save a new Product successfully")
    public void shouldBeAbleToSaveNewProductSuccessfully(){
        ProductDTO productDTO = new ProductDTO("TAB6982", "Table", new BigDecimal("84.32"));

        when(this.productRepository.existsByCode(productDTO.code())).thenReturn(false);
        when(this.productRepository.saveAndFlush(any(Product.class))).thenReturn(this.mockProduct);

        ProductResponseDTO responseDTO = this.productService.save(productDTO);

        verify(this.productRepository).existsByCode("TAB6982");
        verify(this.productRepository).saveAndFlush(any(Product.class));

        assertNotNull(responseDTO);
        assertEquals(UUID.fromString("e1b7ba2a-3851-4432-b7eb-5b517afc7a39"), responseDTO.id());
        assertEquals("TAB6982", responseDTO.code());
        assertEquals("Table", responseDTO.name());
        assertEquals(new BigDecimal("84.32"), responseDTO.price());
    }

    @Test
    @DisplayName("Should throw exception when code already exists")
    public void shouldThrowExceptionWhenCodeAlreadyExists(){
        ProductDTO productDTO = new ProductDTO("TAB6982", "Table", new BigDecimal("84.32"));

        when(this.productRepository.existsByCode(productDTO.code())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productService.save(productDTO));
        assertEquals("Product already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to get product by id")
    public void shouldBeAbleToGetProductById(){
        UUID productId = this.mockProduct.getId();

        when(this.productRepository.findById(productId)).thenReturn(Optional.of(this.mockProduct));

        ProductResponseDTO responseDTO = this.productService.findById(productId);
        assertEquals(responseDTO.id(), this.mockProduct.getId());
        assertAll(
                () -> assertEquals(this.mockProduct.getId(), responseDTO.id()),
                () -> assertEquals(this.mockProduct.getCode(), responseDTO.code()),
                () -> assertEquals(this.mockProduct.getName(), responseDTO.name()),
                () -> assertEquals(this.mockProduct.getPrice(), responseDTO.price())
        );
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    public void shouldThrowExceptionWhenProductNotFound(){
        when(this.productRepository.findById(UUID_MOCK)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productService.findById(UUID_MOCK));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to return all products")
    public void shouldBeAbleToReturnAllProducts(){
        Product product = Product.builder()
                .id(UUID.fromString("1c7ba18d-8b33-43ec-93a5-73813a105519"))
                .code("CHA123")
                .name("Chair")
                .price(new BigDecimal("14.87"))
                .build();

        List<Product> allProducts = List.of(this.mockProduct, product);

        when(this.productRepository.findAll()).thenReturn(allProducts);

        List<ProductResponseDTO> response = this.productService.findAll();
        List<ProductResponseDTO> allProductsToDto = allProducts.stream()
                        .map(p -> new ProductResponseDTO(
                                p.getId(),
                                p.getCode(),
                                p.getName(),
                                p.getPrice()
                        )).toList();

        verify(this.productRepository).findAll();

        assertEquals(allProducts.size(), response.size());
        assertEquals(allProductsToDto, response);
    }

    @Test
    @DisplayName("Should be able to delete product by id successfully")
    public void shouldBeAbleToDeleteProductByIdSuccessfully(){
        when(this.productRepository.deleteProductById(this.mockProduct.getId())).thenReturn(1L);

        this.productService.deleteById(this.mockProduct.getId());

        verify(this.productRepository).deleteProductById(this.mockProduct.getId());
        verify(this.productRepository).flush();
    }

    @Test
    @DisplayName("Should be throw exception when product not exists")
    public void shouldBeThrowExceptionWhenProductNotExists(){
        when(this.productRepository.deleteProductById(UUID_MOCK)).thenReturn(0L);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productService.deleteById(UUID_MOCK));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should be able to update product successfully")
    public void shouldBeAbleToUpdateProductSuccessfully(){
        ProductDTO productUpdated = new ProductDTO("MIR4513", "Mirror", new BigDecimal("33.44"));

        when(this.productRepository.findById(this.mockProduct.getId())).thenReturn(Optional.of(this.mockProduct));
        when(this.productRepository.existsByCodeAndIdNot(
                productUpdated.code(), this.mockProduct.getId())).thenReturn(false);
        when(this.productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ProductResponseDTO response = this.productService.update(this.mockProduct.getId(), productUpdated);

        verify(this.productRepository).findById(this.mockProduct.getId());
        verify(this.productRepository).existsByCodeAndIdNot(productUpdated.code(), this.mockProduct.getId());
        verify(this.productRepository).saveAndFlush(any(Product.class));

        assertEquals(this.mockProduct.getId(), response.id());
        assertEquals(productUpdated.code(), response.code());
        assertEquals(productUpdated.name(), response.name());
        assertEquals(productUpdated.price(), response.price());
    }

    @Test
    @DisplayName("Should throw exception when code already in use")
    public void shouldThrowExceptionWhenCodeAlreadyInUse(){
        ProductDTO productUpdated = new ProductDTO("TAB6982", "Mirror", new BigDecimal("33.44"));
        when(this.productRepository.findById(this.mockProduct.getId())).thenReturn(Optional.of(this.mockProduct));
        when(this.productRepository.existsByCodeAndIdNot(
                productUpdated.code(), this.mockProduct.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productService.update(this.mockProduct.getId(), productUpdated));

        verify(this.productRepository).findById(this.mockProduct.getId());
        verify(this.productRepository).existsByCodeAndIdNot(productUpdated.code(), this.mockProduct.getId());

        assertEquals("Code already in use", exception.getMessage());
    }
}
