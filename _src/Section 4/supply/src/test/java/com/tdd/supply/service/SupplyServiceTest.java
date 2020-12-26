package com.tdd.supply.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.tdd.supply.model.SupplyRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class SupplyServiceTest {

    @Autowired
    private SupplyService supplyService;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setupWireMockServer(){
        // initialize WireMock server
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();

        // configure response stub
        wireMockServer.stubFor(get(urlEqualTo("/supply/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/supply-response.json")));
        wireMockServer.stubFor(get(urlEqualTo("/supply/2"))
                .willReturn(aResponse().withStatus(404)));
        wireMockServer.stubFor(post("/supply/1/saleRecord")
                // Actual Header sent by the RestTemplate is: application/json;charset=UTF-8
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"productId\":1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/supply-response-after-post.json")));
    }

    @AfterEach
    public void stopWireMockServer(){
        wireMockServer.stop();
    }

    @Test
    public void testGetSupplyRecordSuccessfully(){
        Optional<SupplyRecord> supplyRecord = supplyService.getSupplyRecord(1);

        Assertions.assertTrue(supplyRecord.isPresent(), "Supply record should exist");
    }

    @Test
    public void testGetSupplyRecordFails(){
        Optional<SupplyRecord> supplyRecord = supplyService.getSupplyRecord(2);

        Assertions.assertFalse(supplyRecord.isPresent(), "Supply record should not exist");
    }

    @Test
    public void testPurchaseProductSuccessful(){
        Optional<SupplyRecord> supplyRecord = supplyService.purchaseProduct(1, 100);

        Assertions.assertTrue(supplyRecord.isPresent(), "Supply record should exist");
        Assertions.assertEquals(1200, supplyRecord.get().getQuantity().intValue(), "New supply quanity should be 1200");
    }
}
