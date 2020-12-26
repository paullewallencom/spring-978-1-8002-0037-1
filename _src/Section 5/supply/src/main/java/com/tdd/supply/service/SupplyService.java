package com.tdd.supply.service;

import com.tdd.supply.model.SaleRecord;
import com.tdd.supply.model.SupplyRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class SupplyService {

    @Value("${supplyprovider.uri}")
    private String supplyServiceURI;

    // will help us make HTTP requests to other services
    RestTemplate restTemplate = new RestTemplate();

    public Optional<SupplyRecord> getSupplyRecord(Integer id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(supplyServiceURI + "/" + id, SupplyRecord.class));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    public Optional<SupplyRecord> purchaseProduct(Integer productId, Integer quantity) {
        try {
            return Optional.ofNullable(restTemplate.postForObject(supplyServiceURI + "/" + productId + "/purchase",
                    new SaleRecord(productId, quantity),
                    SupplyRecord.class));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }
}
