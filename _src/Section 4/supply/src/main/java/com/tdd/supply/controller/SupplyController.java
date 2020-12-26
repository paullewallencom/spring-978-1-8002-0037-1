package com.tdd.supply.controller;

import com.tdd.supply.model.SaleRecord;
import com.tdd.supply.service.SupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class SupplyController {

    private static final Logger LOGGER = LogManager.getLogger(SupplyController.class);

    @Autowired
    private SupplyService supplyService;

    /**
     * Retrieves supply record for a given id
     * @param id of the supply to search for
     * @return ResponseEntity with found Supply
     */
    @GetMapping("/supply/{id}")
    public ResponseEntity<?> getSupplyRecord(@PathVariable Integer id) {
        LOGGER.info("Finding supply record for product with id:" + id);
        return supplyService.getSupplyRecord(id)
                .map(supplyRecord -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location(new URI("/supply/" + supplyRecord.getProductId()))
                                .body(supplyRecord);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new sale record from a purchase
     * @param saleRecord to be saved
     * @return ResponseEntity with the sale record created
     */
    @PostMapping("/supply/saleRecord")
    public ResponseEntity<?> newSaleRecord(@RequestBody SaleRecord saleRecord) {
        LOGGER.info("Creating new sale record");
        return supplyService.purchaseProduct(saleRecord.getProductId(), saleRecord.getQuantity())
                .map(supplyRecord -> {
                    try {
                        return ResponseEntity
                                .created(new URI("/supply/" + supplyRecord.getProductId()))
                                .body(supplyRecord);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
