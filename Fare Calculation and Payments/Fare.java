package com.RideMate.cab_service.model;

public class Fare {
    private String fareId;
    private double distance;
    private double time;
    private String vehicleType;
    private double amount;
    private double discount;

    // Constructor, Getters, and Setters
    public Fare(String fareId, double distance, double time, String vehicleType, double amount) {
        this.fareId = fareId;
        this.distance = distance;
        this.time = time;
        this.vehicleType = vehicleType;
        this.amount = amount;
        this.discount = 0.0;
    }

    // Getters and Setters
    public String getFareId() { return fareId; }
    public void setFareId(String fareId) { this.fareId = fareId; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public double getTime() { return time; }
    public void setTime(double time) { this.time = time; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

}