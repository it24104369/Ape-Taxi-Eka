package com.RideMate.cab_service.model;

public abstract class FareCalculator {
    protected double baseFare;
    protected double perKmRate;
    protected double perMinuteRate;

    public FareCalculator(double baseFare, double perKmRate, double perMinuteRate) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perMinuteRate = perMinuteRate;
    }

    public abstract double calculateFare(double distance, double time);
}