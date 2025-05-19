package com.RideMate.cab_service.model;

public class CashPayment implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        // Simulate cash payment (always successful for simplicity)
        return true;
    }
}
