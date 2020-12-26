package com.tdd.productsupport.feedback.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Feedback")
public class Feedback {

    private String id;

    private Integer productId;

    private Integer userId;

    private String status;

    private Integer version = 1;

    private String message;

    public Feedback() {
    }

    public Feedback(String id, Integer productId, Integer userId, String status, String message) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.status = status;
        this.message = message;
    }

    public Feedback(String id, Integer productId, Integer userId, String status, Integer version, String message) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.status = status;
        this.version = version;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", productId=" + productId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", version=" + version +
                ", message='" + message + '\'' +
                '}';
    }
}
