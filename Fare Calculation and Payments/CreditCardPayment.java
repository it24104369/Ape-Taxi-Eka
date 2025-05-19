package com.RideMate.cab_service.model;

public class CreditCardPayment implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        // Simulate credit card payment (always successful for simplicity)
        return true;
    }
}
