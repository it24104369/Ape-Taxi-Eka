package com.RideMate.cab_service.model;

public interface PaymentProcessor {
    boolean processPayment(double amount);
}