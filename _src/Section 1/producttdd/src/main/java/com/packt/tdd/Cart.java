package com.packt.tdd;

import java.util.ArrayList;
import java.util.List;

/*
    Represents a typical shopping cart
 */
public class Cart {

    private Float totalAmount = 0F;

    private List<Product> items = new ArrayList<Product>();

    public void addToCart(Product p) {
        items.add(p);
        totalAmount = totalAmount + p.price;
    }

    public void removeFromCart(Product p) {
        items.remove(p);
        totalAmount = totalAmount - p.price;
    }

    public void emptyCart() {
        items.clear();
    }

    public List<Product> getItems(){
        return items;
    }

    public Float getTotalAmount(){
        return totalAmount;
    }

    public void printCartDetails(){
        System.out.println("Here are the items in your shopping cart:");
        for (Product p : getItems()){
            System.out.println("===================");
            System.out.println("Product ID:" + p.id);
            System.out.println("Product Name:" + p.name);
            System.out.println("Product Description:" + p.description);
            System.out.println("Product Price:$" + p.price);
            System.out.println("===================");
        }
        System.out.println("Shopping Cart Total:$" + totalAmount);
    }
}
