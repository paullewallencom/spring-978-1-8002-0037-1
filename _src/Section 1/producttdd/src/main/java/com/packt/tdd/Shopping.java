package com.packt.tdd;

/*
    Represents a typical shopping process, from adding to cart to payment and checkout
 */
public class Shopping {

    public static void main(String[] args) {
        Product firstProduct = new Product(1, "USB Drive", "128 GB USB Drive", 19.9F);
        Product secondProduct = new Product(2, "External Hard Drive", "1 TB External Drive", 79.9F);

        Checkout checkout = new Checkout();
        checkout.addToCart(firstProduct);
        checkout.addToCart(secondProduct);

        checkout.pay(90F);
        checkout.complete();
    }
}
