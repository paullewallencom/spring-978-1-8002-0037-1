package com.tdd.products.products.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.products.products.model.Product;
import com.tdd.products.products.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    File DATA_JSON = Paths.get("src", "test", "resources", "products.json").toFile();

    @BeforeEach
    public void setup() throws IOException {
        // Deserialize products from JSON file to Product array
        Product[] products = new ObjectMapper().readValue(DATA_JSON, Product[].class);

        // Save each product to database
        Arrays.stream(products).forEach(productRepository::save);
    }

    @AfterEach
    public void cleanup(){
        // Cleanup the database after each test
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Test product found - GET /products/1")
    public void testGetProductByIdFindsProduct() throws Exception {
        // Perform GET request
        mockMvc.perform(get("/products/{id}", 1))
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.ETAG,  "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))

                // Validate response body
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("First Product")))
                .andExpect(jsonPath("$.description", is("First Product Description")))
                .andExpect(jsonPath("$.quantity", is(8)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("Test all products found - GET /products")
    public void testAllProductsFound() throws Exception {
        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response body
                .andExpect(jsonPath("$[0].name", is("First Product")))
                .andExpect(jsonPath("$[1].name", is("Second Product")));
    }

    @Test
    @DisplayName("Add a new product - POST /products")
    public void testAddNewProduct() throws Exception {
        // Prepare product to save
        Product newProduct = new Product("New Product", "New Product Description", 8);

        // Perform POST request
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(newProduct)))

                // Validate 201 CREATED and JSON response type received
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/3"))

                // Validate response body
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.quantity", is(8)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("Update an existing product with success - PUT /products/1")
    public void testUpdatingProductWithSuccess() throws Exception {
        // Prepare product to update
        Product productToUpdate = new Product("New name", "New description", 20);

        // Perform PUT request
        mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(new ObjectMapper().writeValueAsString(productToUpdate)))

                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))

                // Validate response body
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New name")))
                .andExpect(jsonPath("$.quantity", is(20)));
    }

    @Test
    @DisplayName("Version mismatch while updating existing product - PUT /products/1")
    public void testVersionMismatchWhileUpdating() throws Exception {
        // Prepare product to update
        Product productToUpdate = new Product("New name", "New description", 20);

        // Perform PUT request
        mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 2)
                .content(new ObjectMapper().writeValueAsString(productToUpdate)))

                // Validate 409 CONFLICT received
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Product not found while updating - PUT /products/1")
    public void testProductNotFoundWhileUpdating() throws Exception{
        // Prepare product to update
        Product productToUpdate = new Product("New name", "New description", 20);

        // Perform PUT request
        mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(new ObjectMapper().writeValueAsString(productToUpdate)))

                // Validate 404 NOT_FOUND received
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a product successfully - DELETE /products/1")
    public void testProductDeletedSuccessfully() throws Exception {
        // Perform DELETE request
        mockMvc.perform(delete("/products/{id}", 2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Fail to delete an non-existing product - DELETE /products/1")
    public void testFailureToDeleteNonExistingProduct() throws Exception {
        // Perform DELETE request
        mockMvc.perform(delete("/products/{id}", 20))
                .andExpect(status().isNotFound());
    }
}
