package com.autoflex.autoflex.integration;

import com.autoflex.autoflex.dto.ProductRawMaterialInputDTO;
import com.autoflex.autoflex.dto.ProductWithMaterialInputDTO;
import com.autoflex.autoflex.model.Product;
import com.autoflex.autoflex.model.ProductRawMaterial;
import com.autoflex.autoflex.model.RawMaterial;
import com.autoflex.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.autoflex.repository.ProductRepository;
import com.autoflex.autoflex.repository.RawMaterialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
public class ProductRawMaterialIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private ProductRawMaterialRepository productRawMaterialRepository;

    private Product savedProduct;
    private RawMaterial savedRawMaterial;

    @BeforeEach
    public void setUp(){
        this.productRawMaterialRepository.deleteAll();
        this.productRepository.deleteAll();
        this.rawMaterialRepository.deleteAll();

        this.savedProduct = this.productRepository.save(Product.builder()
                .code("LLL15dss")
                .name("Chair")
                .price(new BigDecimal("44.0"))
                .rawMaterials(new ArrayList<>())
                .build());

        this.savedRawMaterial = this.rawMaterialRepository.save(RawMaterial.builder()
                .code("WOOD001")
                .name("Wood")
                .stockQuantity(23)
                .build());
    }

    @Test
    @DisplayName("Should associate product with raw materials")
    public void shouldAssociateProductWithRawMaterials() throws Exception {
        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(
                this.savedProduct.getId(), List.of(new ProductRawMaterialInputDTO(this.savedRawMaterial.getId(), 2))
        );

        this.mockMvc.perform(post("/api/v1/products-raw-materials/associate")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(this.savedProduct.getId().toString()))
                .andExpect(jsonPath("$.code").value(this.savedProduct.getCode()))
                .andExpect(jsonPath("$.name").value(this.savedProduct.getName()))
                .andExpect(jsonPath("$.price").value(this.savedProduct.getPrice().intValue()))
                .andExpect(jsonPath("$.rawMaterials[0].rawMaterialId")
                        .value(this.savedRawMaterial.getId().toString()))
                .andExpect(jsonPath("$.rawMaterials[0].rawMaterialName")
                        .value(this.savedRawMaterial.getName()))
                .andExpect(jsonPath("$.rawMaterials[0].stockQuantity")
                        .value(this.savedRawMaterial.getStockQuantity()))
                .andExpect(jsonPath("$.rawMaterials[0].requiredQuantity")
                        .value(2))
                .andExpect(jsonPath("$.rawMaterials[0].productRawMaterialId").exists());

        assertEquals(1, this.productRawMaterialRepository.count());
    }

    @Test
    @DisplayName("Should return 404 when product does not exist")
    public void shouldReturn404WhenProductNotFound() throws Exception {
        ProductWithMaterialInputDTO inputDTO = new ProductWithMaterialInputDTO(
                UUID.randomUUID(), List.of(new ProductRawMaterialInputDTO(this.savedRawMaterial.getId(), 2)));

        this.mockMvc.perform(post("/api/v1/products-raw-materials/associate")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found"));
    }

    @Test
    @DisplayName("Should return 400 when raw material is already associated")
    public void shouldReturn400WhenRawMaterialAlreadyAssociated() throws Exception {
        ProductRawMaterialInputDTO existingAssociation = new ProductRawMaterialInputDTO(this.savedRawMaterial.getId(), 2);
        ProductWithMaterialInputDTO firstInput = new ProductWithMaterialInputDTO(this.savedProduct.getId(),
                List.of(existingAssociation));

        this.mockMvc.perform(post("/api/v1/products-raw-materials/associate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(firstInput)))
                .andExpect(status().isCreated());

        ProductWithMaterialInputDTO duplicateInput = new ProductWithMaterialInputDTO(this.savedProduct.getId(),
                List.of(existingAssociation));

        this.mockMvc.perform(post("/api/v1/products-raw-materials/associate")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(duplicateInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/invalid-data"))
                .andExpect(jsonPath("$.title").value("Invalid data"))
                .andExpect(jsonPath("$.detail").value("One or more raw materials are already associated with this product"));
    }

    @Test
    @DisplayName("Should return suggested production available for products")
    void shouldReturnSuggestedProductionAvailable() throws Exception {
        Product productTable = this.productRepository.save(Product.builder()
                .code("TAB784")
                .name("Table")
                .price(new BigDecimal("84.32"))
                .rawMaterials(new ArrayList<>())
                .build());

        ProductRawMaterial tablePrm = ProductRawMaterial.builder()
                .product(productTable)
                .rawMaterial(this.savedRawMaterial)
                .requiredQuantity(3)
                .build();
        productTable.getRawMaterials().add(tablePrm);
        this.productRepository.save(productTable);

        ProductRawMaterial chairPrm = ProductRawMaterial.builder()
                .product(this.savedProduct)
                .rawMaterial(this.savedRawMaterial)
                .requiredQuantity(2)
                .build();
        this.savedProduct.getRawMaterials().add(chairPrm);
        this.productRepository.save(this.savedProduct);

        this.mockMvc.perform(get("/api/v1/products-raw-materials/suggested-production-available")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productTable.getId().toString()))
                .andExpect(jsonPath("$[0].code").value(productTable.getCode()))
                .andExpect(jsonPath("$[0].name").value(productTable.getName()))
                .andExpect(jsonPath("$[0].price").value(productTable.getPrice()))
                .andExpect(jsonPath("$[0].quantityProductionAvailable").value(7))
                .andExpect(jsonPath("$[0].totalValue").value(590.24))

                .andExpect(jsonPath("$[1].productId").value(this.savedProduct.getId().toString()))
                .andExpect(jsonPath("$[1].code").value(this.savedProduct.getCode()))
                .andExpect(jsonPath("$[1].name").value(this.savedProduct.getName()))
                .andExpect(jsonPath("$[1].price").value(this.savedProduct.getPrice()))
                .andExpect(jsonPath("$[1].quantityProductionAvailable").value(1))
                .andExpect(jsonPath("$[1].totalValue").value(44.0));
    }

}
