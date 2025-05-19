package com.RideMate.cab_service.model;

public class DigitalWalletPayment implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        // Simulate digital wallet payment (always successful for simplicity)
        return true;
    }
}
