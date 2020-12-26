package com.tdd.products.products.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.products.products.model.Product;
import com.tdd.products.products.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductsControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test product found - GET /products/1")
    public void testGetProductByIdFindsProduct() throws Exception {
        // Prepare mock product
        Product mockProduct = new Product(1,"My product", "Details of my product", 5, 1);

        // Prepare mocked service method
        doReturn(mockProduct).when(productService).findById(mockProduct.getId());

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", 1))
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.ETAG,  "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))

                // Validate response body
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("My product")))
                .andExpect(jsonPath("$.description", is("Details of my product")))
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("Test all products found - GET /products")
    public void testAllProductsFound() throws Exception {
        // Prepare mock products
        Product firstProduct = new Product(1, "First Product", "First Product Description", 8, 1);
        Product secondProduct = new Product(2, "Second Product", "Second Product Description", 10, 1);

        List<Product> products = new ArrayList<>();
        products.add(firstProduct);
        products.add(secondProduct);

        // Prepare mock service method
        doReturn(products).when(productService).findAll();

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
        // Prepare mock product
        Product newProduct = new Product("New Product", "New Product Description", 8);
        Product mockProduct = new Product(1, "New Product", "New Product Description", 8, 1);

        // Prepare mock service method
        doReturn(mockProduct).when(productService).save(ArgumentMatchers.any());

        // Perform POST request
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(newProduct)))

                // Validate 201 CREATED and JSON response type received
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))

                // Validate response body
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.quantity", is(8)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("Update an existing product with success - PUT /products/1")
    public void testUpdatingProductWithSuccess() throws Exception {
        // Prepare mock product
        Product productToUpdate = new Product("New name", "New description", 20);
        Product mockProduct = new Product(1, "Mock product", "Mock product desc", 10, 1);

        // Prepare mock service methods
        doReturn(mockProduct).when(productService).findById(1);
        doReturn(mockProduct).when(productService).update(ArgumentMatchers.any());

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
        // Prepare mock product
        Product productToUpdate = new Product("New name", "New description", 20);
        Product mockProduct = new Product(1, "Mock product", "Mock product desc", 10, 2);

        // Prepare mock service method
        doReturn(mockProduct).when(productService).findById(1);

        // Perform PUT request
        mockMvc.perform(put("/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(new ObjectMapper().writeValueAsString(productToUpdate)))

                // Validate 409 CONFLICT received
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Product not found while updating - PUT /products/1")
    public void testProductNotFoundWhileUpdating() throws Exception{
        // Prepare mock product
        Product productToUpdate = new Product("New name", "New description", 20);

        // Prepare mock service method
        doReturn(null).when(productService).findById(1);

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
        // Prepare mock product
        Product existingProduct = new Product(1, "New name", "New description", 20, 1);

        // Prepare mock service method
        doReturn(existingProduct).when(productService).findById(1);

        // Perform DELETE request
        mockMvc.perform(delete("/products/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Fail to delete an non-existing product - DELETE /products/1")
    public void testFailureToDeleteNonExistingProduct() throws Exception {
        // Prepare mock service method
        doReturn(null).when(productService).findById(1);

        // Perform DELETE request
        mockMvc.perform(delete("/products/{id}", 1))
                .andExpect(status().isNotFound());
    }
}
