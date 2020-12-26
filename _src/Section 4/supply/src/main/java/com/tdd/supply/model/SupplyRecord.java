package com.tdd.supply.model;

public class SupplyRecord {
    private Integer productId;
    private String productName;
    private String productCategory;
    private Integer quantity;

    public SupplyRecord() { }

    public SupplyRecord(Integer productId, String productName, String productCategory, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
