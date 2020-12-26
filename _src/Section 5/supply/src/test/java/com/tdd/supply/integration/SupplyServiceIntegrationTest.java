package com.tdd.supply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.tdd.supply.model.SaleRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = "classpath:test.properties")
public class SupplyServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setupWireMockServer(){
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
    }

    @AfterEach
    public void stopWireMockServer(){
        wireMockServer.stop();
    }

    @Test
    @DisplayName("GET /supply/1 - Successful")
    public void testGetSupplyRecordSuccessfully() throws Exception {
        // Perform the request
        mockMvc.perform(get("/supply/{id}", 1))
                // Validate status and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate header
                .andExpect(header().string(HttpHeaders.LOCATION, "/supply/1"))

                // Validate response body
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("New Product")))
                .andExpect(jsonPath("$.productCategory", is("Utilities")))
                .andExpect(jsonPath("$.quantity", is(1300)));
    }

    @Test
    @DisplayName("GET /supply/1 - Failure")
    public void testGetSupplyRecordFailure() throws Exception {
        // Perform the request
        mockMvc.perform(get("/supply/{id}", 50))

                // Validate status
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST - /supply/saleRecord - Successful")
    public void testAddNewSaleRecordSuccessfully() throws Exception {
        // Perform the request
        mockMvc.perform(post("/supply/saleRecord")
                .content(new ObjectMapper().writeValueAsString(new SaleRecord(1, 100)))
                .contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(header().string(HttpHeaders.LOCATION, "/supply/1"))

                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("New Product")))
                .andExpect(jsonPath("$.productCategory", is("Utilities")))
                .andExpect(jsonPath("$.quantity", is(1200)));
    }
}
