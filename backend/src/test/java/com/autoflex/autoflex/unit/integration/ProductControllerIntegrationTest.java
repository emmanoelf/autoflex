package com.autoflex.autoflex.unit.integration;

import com.autoflex.autoflex.dto.ProductDTO;
import com.autoflex.autoflex.dto.ProductResponseDTO;
import com.autoflex.autoflex.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp(){
        this.productRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create a product successfully")
    public void shouldCreateProductSuccessfully() throws Exception {
        ProductDTO productDTO = new ProductDTO("TAB6982", "Table", new BigDecimal("84.32"));

        this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TAB6982"))
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.price").value(84.32));

        assertEquals(1, this.productRepository.count());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when product code already exists")
    public void shouldReturnBadRequestWhenProductCodeAlreadyExists() throws Exception {
        ProductDTO existingProduct = new ProductDTO("TAB1234", "Chair", new BigDecimal("75.00"));
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(existingProduct)))
                .andExpect(status().isCreated());

        ProductDTO duplicateProduct = new ProductDTO("TAB1234", "Table", new BigDecimal("120.50"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicateProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/invalid-data"))
                .andExpect(jsonPath("$.title").value("Invalid data"))
                .andExpect(jsonPath("$.detail").value("Product already exists"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when product does not exist")
    public void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        this.mockMvc.perform(get("/api/v1/products/{productId}", nonExistentId)
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found"));
    }

    @Test
    @DisplayName("Should return a product successfully when it exists")
    public void shouldReturnProductWhenExists() throws Exception {
        ProductDTO productDTO = new ProductDTO("TAB1234", "Table", new BigDecimal("120.50"));
        String content = this.objectMapper.writeValueAsString(productDTO);

        String response = this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductResponseDTO createdProduct = this.objectMapper.readValue(response, ProductResponseDTO.class);

        this.mockMvc.perform(get("/api/v1/products/{productId}", createdProduct.id())
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.id().toString()))
                .andExpect(jsonPath("$.code").value("TAB1234"))
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.price").value(120.50));
    }

    @Test
    @DisplayName("Should return all products successfully")
    public void shouldReturnAllProductsSuccessfully() throws Exception {
        ProductDTO product1 = new ProductDTO("TAB1001", "Chair", new BigDecimal("50.00"));
        ProductDTO product2 = new ProductDTO("TAB1002", "Table", new BigDecimal("120.00"));

        this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(product2)))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/products/")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].code").value("TAB1001"))
                .andExpect(jsonPath("$[0].name").value("Chair"))
                .andExpect(jsonPath("$[0].price").value(50.00))
                .andExpect(jsonPath("$[1].code").value("TAB1002"))
                .andExpect(jsonPath("$[1].name").value("Table"))
                .andExpect(jsonPath("$[1].price").value(120.00));
    }

    @Test
    @DisplayName("Should delete product successfully")
    public void shouldDeleteProductSuccessfully() throws Exception {
        ProductDTO productDTO = new ProductDTO("TAB123", "Table", new BigDecimal("100.00"));
        String response = this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID productId = this.objectMapper.readTree(response).get("id").asText().equals("") ?
                UUID.randomUUID() :
                UUID.fromString(this.objectMapper.readTree(response).get("id").asText());

        this.mockMvc.perform(delete("/api/v1/products/{productId}", productId))
                .andExpect(status().isNoContent());

        assertEquals(0, productRepository.count());
    }

    @Test
    @DisplayName("Should return 404 when product does not exist")
    public void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        this.mockMvc.perform(delete("/api/v1/products/{productId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found"));
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductDTO original = new ProductDTO("TAB123", "Table", new BigDecimal("50.0"));
        String originalJson = this.objectMapper.writeValueAsString(original);

        MvcResult result = this.mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(originalJson))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponseDTO saved = this.objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProductResponseDTO.class
        );

        ProductDTO updateDTO = new ProductDTO("NEWCODE", "New Table", new BigDecimal("99.99"));
        this.mockMvc.perform(put("/api/v1/products/{productId}", saved.id())
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(updateDTO))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NEWCODE"))
                .andExpect(jsonPath("$.name").value("New Table"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @DisplayName("Should return 404 Not Found when product does not exist")
    void shouldReturnNotFoundWhenUpdatingNonexistentProduct() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        ProductDTO dto = new ProductDTO("CODE", "Table", new BigDecimal("50.0"));

        this.mockMvc.perform(put("/api/v1/products/{productId}", nonExistentId)
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(dto))
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").value("https://autoflex.com.br/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found"));
    }
}
