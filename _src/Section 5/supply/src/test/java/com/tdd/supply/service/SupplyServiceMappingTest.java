package com.tdd.supply.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.tdd.supply.model.SaleRecord;
import com.tdd.supply.model.SupplyRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class SupplyServiceMappingTest {

    @Autowired
    private SupplyService supplyService;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void startWireMockServer(){
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
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
