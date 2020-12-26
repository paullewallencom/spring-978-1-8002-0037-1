package com.packt.tdd;

/*
    Represents a product in a shopping cart
 */
public class Product {

    Integer id;
    String name;
    String description;
    Float price;

    public Product() {
    }

    public Product(Integer id, String name, String description, Float price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        Product p = (Product) o;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
