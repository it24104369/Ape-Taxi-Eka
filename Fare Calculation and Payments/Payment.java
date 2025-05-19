package com.RideMate.cab_service.model;

public class Payment {
    private String paymentId;
    private String fareId;
    private double amount;
    private String paymentMethod; // cash, credit_card, digital_wallet
    private String status; // pending, completed, refunded

    // Constructor, Getters, and Setters
    public Payment(String paymentId, String fareId, double amount, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.fareId = fareId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getFareId() { return fareId; }
    public void setFareId(String fareId) { this.fareId = fareId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}