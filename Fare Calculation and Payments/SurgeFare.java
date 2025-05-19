package com.RideMate.cab_service.model;
public class SurgeFare extends FareCalculator {
    private double surgeMultiplier;

    public SurgeFare(double surgeMultiplier) {
        super(5.0, 1.0, 0.2);
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateFare(double distance, double time) {
        double standardFare = baseFare + (perKmRate * distance) + (perMinuteRate * time);
        return standardFare * surgeMultiplier;
    }
}