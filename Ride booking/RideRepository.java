package com.ridebooking.repository;

import com.ridebooking.model.Ride;
import com.ridebooking.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RideRepository {
    private static final String RIDES_FILE = "src/main/resources/data/rides.txt";

    public RideRepository() {
        // Ensure the data directory exists
        File dataDir = new File("src/main/resources/data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Make sure the rides file exists
        File ridesFile = new File(RIDES_FILE);
        if (!ridesFile.exists()) {
            try {
                ridesFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating rides file: " + e.getMessage());
            }
        }
    }

    public List<Ride> findAll() {
        try {
            List<String> lines = FileUtil.readAllLines(RIDES_FILE);
            return lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .map(Ride::fromCsvString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading rides file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Ride> findById(String id) {
        System.out.println("Repository looking for ride with ID: " + id);

        List<Ride> rides = findAll();
        for (Ride ride : rides) {
            if (ride.getId().equals(id)) {
                System.out.println("Repository found ride: " + ride.getId() + ", Status: " + ride.getStatus());
                return Optional.of(ride);
            }
        }

        System.out.println("Repository did not find ride with ID: " + id);
        return Optional.empty();
    }

    public Ride save(Ride ride) {
        List<Ride> rides = findAll();

        // Debug log
        System.out.println("Repository saving ride: " + ride.getId() + ", Status: " + ride.getStatus());

        // Check if ride already exists (update)
        boolean exists = false;
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getId().equals(ride.getId())) {
                rides.set(i, ride);
                exists = true;
                System.out.println("Repository updated existing ride");
                break;
            }
        }

        // If ride doesn't exist, add it
        if (!exists) {
            rides.add(ride);
            System.out.println("Repository added new ride");
        }

        try {
            List<String> lines = rides.stream()
                    .map(Ride::toCsvString)
                    .collect(Collectors.toList());

            // Debug log
            System.out.println("Writing " + lines.size() + " rides to file");
            for (String line : lines) {
                System.out.println(line);
            }

            FileUtil.writeAllLines(RIDES_FILE, lines);
            return ride;
        } catch (IOException e) {
            System.err.println("Error writing rides file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteById(String id) {
        List<Ride> rides = findAll();
        boolean removed = rides.removeIf(ride -> ride.getId().equals(id));

        if (removed) {
            try {
                List<String> lines = rides.stream()
                        .map(Ride::toCsvString)
                        .collect(Collectors.toList());
                FileUtil.writeAllLines(RIDES_FILE, lines);
                return true;
            } catch (IOException e) {
                System.err.println("Error writing rides file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    public List<Ride> findByDriverId(String driverId) {
        return findAll().stream()
                .filter(ride -> driverId != null && driverId.equals(ride.getDriverId()))
                .collect(Collectors.toList());
    }

    public List<Ride> findByPassengerId(String passengerId) {
        return findAll().stream()
                .filter(ride -> passengerId != null && passengerId.equals(ride.getPassengerId()))
                .collect(Collectors.toList());
    }

    public List<Ride> findByStatus(String status) {
        return findAll().stream()
                .filter(ride -> status != null && status.equals(ride.getStatus()))
                .collect(Collectors.toList());
    }
}