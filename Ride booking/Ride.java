package com.ridebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride implements Serializable {
    private String id;
    private String passengerId;
    private String passengerName;
    private String passengerPhone;
    private String driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime requestTime;
    private LocalDateTime completionTime;
    private double fare;
    private String status; // REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED

    public Ride(String passengerName, String passengerPhone, String pickupLocation,
                String dropoffLocation, double fare) {
        this.id = UUID.randomUUID().toString();
        this.passengerId = UUID.randomUUID().toString(); // In a real app, this would be the logged-in user's ID
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.requestTime = LocalDateTime.now();
        this.fare = fare;
        this.status = "REQUESTED";
    }

    // Convert to CSV format
    public String toCsvString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String requestTimeStr = requestTime != null ? requestTime.format(formatter) : "";
        String completionTimeStr = completionTime != null ? completionTime.format(formatter) : "";

        return String.join(",", id, passengerId, passengerName, passengerPhone,
                driverId != null ? driverId : "",
                pickupLocation, dropoffLocation,
                requestTimeStr, completionTimeStr,
                String.valueOf(fare), status);
    }

    // Create from CSV string
    // Create from CSV string
    public static Ride fromCsvString(String csvLine) {
        String[] data = csvLine.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        Ride ride = new Ride();

        // Set default values
        ride.setId(UUID.randomUUID().toString());
        ride.setStatus("REQUESTED");

        // Parse data if available
        if (data.length > 0) ride.setId(data[0].equals("null") ? UUID.randomUUID().toString() : data[0]);
        if (data.length > 1) ride.setPassengerId(data[1].equals("null") ? UUID.randomUUID().toString() : data[1]);
        if (data.length > 2) ride.setPassengerName(data[2]);
        if (data.length > 3) ride.setPassengerPhone(data[3]);
        if (data.length > 4) ride.setDriverId(data[4].isEmpty() || data[4].equals("null") ? null : data[4]);
        if (data.length > 5) ride.setPickupLocation(data[5]);
        if (data.length > 6) ride.setDropoffLocation(data[6]);

        // Parse dates
        if (data.length > 7 && !data[7].isEmpty() && !data[7].equals("null")) {
            try {
                ride.setRequestTime(LocalDateTime.parse(data[7], formatter));
            } catch (Exception e) {
                ride.setRequestTime(LocalDateTime.now());
            }
        } else {
            ride.setRequestTime(LocalDateTime.now());
        }

        if (data.length > 8 && !data[8].isEmpty() && !data[8].equals("null")) {
            try {
                ride.setCompletionTime(LocalDateTime.parse(data[8], formatter));
            } catch (Exception e) {
                ride.setCompletionTime(null);
            }
        }

        // Parse fare
        if (data.length > 9) {
            try {
                ride.setFare(Double.parseDouble(data[9]));
            } catch (NumberFormatException e) {
                ride.setFare(0.0);
            }
        }

        // Parse status - this is critical
        if (data.length > 10 && !data[10].isEmpty() && !data[10].equals("null")) {
            ride.setStatus(data[10]);
        }

        return ride;
    }
}