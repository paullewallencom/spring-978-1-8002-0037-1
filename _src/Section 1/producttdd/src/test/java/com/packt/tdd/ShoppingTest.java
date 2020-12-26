package com.packt.tdd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ShoppingTest {

    private Product firstProduct;
    private Product secondProduct;
    private Checkout checkout;

    @BeforeEach
    @DisplayName("Given 2 products in the shopping cart")
    public void setup(){
        firstProduct = new Product(1, "USB Drive", "128 GB USB Drive", 19.9F);
        secondProduct = new Product(2, "External Hard Drive", "1 TB External Drive", 79.9F);
        checkout = new Checkout();
        checkout.addToCart(firstProduct);
        checkout.addToCart(secondProduct);
    }

    @Test
    @DisplayName("Test if products added to shopping cart successfully")
    public void testItemsAddedToShoppingCart(){
        // Given two products in the shopping cart

        // When
        Product newProduct = new Product(3, "New External Hard Drive", "1 TB External Drive", 89.9F);
        checkout.addToCart(newProduct);

        // Then
        Assertions.assertEquals(3, checkout.getItems().size());
    }

    @Test
    @DisplayName("Test if shopping cart total amount is calculated correctly")
    public void testShoppingCartAmount(){
        // Given two products in the shopping cart

        // Then
        Assertions.assertEquals(2, checkout.getItems().size());
        Assertions.assertEquals(99.8F, (float) checkout.getTotalAmount());
    }

    @Test
    @DisplayName("Test if due amount is calculated correctly")
    public void testDueAmountCalculation(){
        // Given two products in the shopping cart

        // When
        checkout.pay(90.8F);

        // Then
        Assertions.assertEquals(9.0F, (float) checkout.getPaymentDue());
    }

    @Test
    @DisplayName("Test if product removed from shopping cart correctly")
    public void testProductRemovalFromCart(){
        // Given two products in the shopping cart

        // When
        checkout.removeFromCart(firstProduct);

        // Then
        Assertions.assertEquals(1, checkout.getItems().size());
        Assertions.assertEquals(79.9F, (float) checkout.getTotalAmount());
    }

    @Test
    @DisplayName("Test if payment status is correct")
    public void testIfPaymentStatusIsCorrect(){
        // Given two products in the shopping cart

        // When
        checkout.pay(99.8F);
        checkout.complete();

        // Then
        Assertions.assertEquals(Checkout.PaymentStatus.DONE, checkout.getPaymentStatus());
    }
}
