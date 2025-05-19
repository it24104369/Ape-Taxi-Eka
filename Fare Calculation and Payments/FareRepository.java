package com.RideMate.cab_service.repository;
import com.RideMate.cab_service.model.Fare;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Repository
public class FareRepository {
    private static final String FILE_PATH = "data/fares.txt";

    public Fare save(Fare fare) throws IOException {
        fare.setFareId(UUID.randomUUID().toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(fare.getFareId() + "," + fare.getDistance() + "," + fare.getTime() + "," +
                       fare.getVehicleType() + "," + fare.getAmount() + "," + fare.getDiscount());
            writer.newLine();
        }
        return fare;
    }

    public Fare findById(String fareId) throws IOException {
        List<Fare> fares = findAll();
        return fares.stream().filter(f -> f.getFareId().equals(fareId)).findFirst().orElse(null);
    }

    public List<Fare> findAll() throws IOException {
        List<Fare> fares = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Fare fare = new Fare(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                                     parts[3], Double.parseDouble(parts[4]));
                fare.setDiscount(Double.parseDouble(parts[5]));
                fares.add(fare);
            }
        }
        return fares;
    }

    public Fare update(Fare fare) throws IOException {
        List<Fare> fares = findAll();
        fares.removeIf(f -> f.getFareId().equals(fare.getFareId()));
        fares.add(fare);
        saveAll(fares);
        return fare;
    }

    private void saveAll(List<Fare> fares) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Fare fare : fares) {
                writer.write(fare.getFareId() + "," + fare.getDistance() + "," + fare.getTime() + "," +
                           fare.getVehicleType() + "," + fare.getAmount() + "," + fare.getDiscount());
                writer.newLine();
            }
        }
    }
}
