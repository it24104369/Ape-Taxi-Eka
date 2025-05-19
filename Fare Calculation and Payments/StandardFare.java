package com.RideMate.cab_service.model;

public class StandardFare extends FareCalculator {
    public StandardFare() {
        super(5.0, 1.0, 0.2); // Example rates: $5 base, $1/km, $0.2/min
    }

    @Override
    public double calculateFare(double distance, double time) {
        return baseFare + (perKmRate * distance) + (perMinuteRate * time);
    }
}