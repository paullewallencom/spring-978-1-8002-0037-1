package com.packt.tdd;

import java.util.Date;

/*
    Represents the checkout process for a shopping cart
    Extends on the functionality of Cart
 */
public class Checkout extends Cart {

    private Float paymentAmount = 0F;

    private Float paymentDue = 0F;

    private Date paymentDate;

    public enum PaymentStatus{
        DUE,DONE
    };

    private PaymentStatus paymentStatus;

    public Float getPaymentDue(){
        return paymentDue;
    }

    public PaymentStatus getPaymentStatus(){
        return paymentStatus;
    }

    public void pay(Float payment){
        paymentAmount = payment;
        paymentDue = getTotalAmount() - paymentAmount;
        paymentDate = new Date();
    }

    public void confirmOrder(){
        if (paymentDue == 0.0F){
            paymentStatus = PaymentStatus.DONE;
            System.out.println("Payment Successful! Thank you for your order.");
        } else if (paymentDue > 0){
            paymentStatus = PaymentStatus.DUE;
            System.out.printf("Payment Failed! Remaining $%f needs to be paid.", paymentDue);
        }
    }

    public void complete(){
        printCartDetails();
        confirmOrder();
    }
}
