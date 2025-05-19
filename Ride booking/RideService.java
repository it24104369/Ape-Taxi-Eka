package com.ridebooking.service;

import com.ridebooking.model.Ride;
import com.ridebooking.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Optional<Ride> getRideById(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("Invalid ride ID: null or empty");
            return Optional.empty();
        }

        // Add debug log to trace the ride lookup
        System.out.println("Looking up ride with ID: " + id);
        Optional<Ride> ride = rideRepository.findById(id);

        if (ride.isPresent()) {
            System.out.println("Found ride: " + ride.get().getId() + ", Status: " + ride.get().getStatus());
        } else {
            System.out.println("Ride not found with ID: " + id);
        }

        return ride;
    }

    public List<Ride> getRidesByDriverId(String driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> getRidesByPassengerId(String passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    public List<Ride> getRidesByStatus(String status) {
        return rideRepository.findByStatus(status);
    }

    public Ride createRide(Ride ride) {
        if (ride == null) {
            return null;
        }

        // Ensure the ride has an ID
        if (ride.getId() == null || ride.getId().trim().isEmpty()) {
            ride.setId(java.util.UUID.randomUUID().toString());
        }

        // Ensure request time is set
        if (ride.getRequestTime() == null) {
            ride.setRequestTime(LocalDateTime.now());
        }

        // Ensure status is set
        if (ride.getStatus() == null || ride.getStatus().trim().isEmpty()) {
            ride.setStatus("REQUESTED");
        }

        System.out.println("Creating new ride with ID: " + ride.getId());
        return rideRepository.save(ride);
    }

    public Ride updateRide(Ride ride) {
        if (ride == null || ride.getId() == null) {
            System.out.println("Cannot update null ride or ride with null ID");
            return null;
        }

        // Verify the ride exists before updating
        Optional<Ride> existingRide = rideRepository.findById(ride.getId());
        if (!existingRide.isPresent()) {
            System.out.println("Cannot update - ride not found with ID: " + ride.getId());
            return null;
        }

        System.out.println("Updating ride with ID: " + ride.getId() + ", Status: " + ride.getStatus());
        return rideRepository.save(ride);
    }

    public boolean deleteRide(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return rideRepository.deleteById(id);
    }

    public Ride assignDriverToRide(String rideId, String driverId) {
        if (rideId == null || driverId == null) {
            System.out.println("Cannot assign driver - null ride ID or driver ID");
            return null;
        }

        System.out.println("Assigning driver " + driverId + " to ride " + rideId);
        Optional<Ride> optionalRide = getRideById(rideId);

        if (optionalRide.isPresent()) {
            Ride ride = optionalRide.get();
            ride.setDriverId(driverId);
            ride.setStatus("ACCEPTED");
            return updateRide(ride);
        } else {
            System.out.println("Cannot assign driver - ride not found with ID: " + rideId);
            return null;
        }
    }

    public Ride updateRideStatus(String rideId, String status) {
        if (rideId == null || status == null) {
            System.out.println("Cannot update status - null ride ID or status");
            return null;
        }

        System.out.println("Updating ride " + rideId + " status to " + status);
        Optional<Ride> optionalRide = getRideById(rideId);

        if (optionalRide.isPresent()) {
            Ride ride = optionalRide.get();

            // Debug log
            System.out.println("Found ride to update: " + ride.getId() +
                    ", current status: " + ride.getStatus() +
                    ", new status: " + status);

            // Set the new status
            ride.setStatus(status);

            // Set completion time for completed or cancelled rides
            if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
                ride.setCompletionTime(LocalDateTime.now());
                System.out.println("Setting completion time: " + ride.getCompletionTime());
            }

            // Save the updated ride
            Ride savedRide = rideRepository.save(ride);

            // Debug log
            if (savedRide != null) {
                System.out.println("Ride successfully saved with status: " + savedRide.getStatus());
            } else {
                System.out.println("Failed to save ride after status update");
            }

            return savedRide;
        } else {
            System.out.println("Cannot update status - ride not found with ID: " + rideId);
            return null;
        }
    }
}