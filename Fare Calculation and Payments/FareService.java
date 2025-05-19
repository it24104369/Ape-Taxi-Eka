package com.RideMate.cab_service.service;
import com.RideMate.cab_service.model.Fare;
import com.RideMate.cab_service.model.FareCalculator;
import com.RideMate.cab_service.model.StandardFare;
import com.RideMate.cab_service.model.SurgeFare;
import com.RideMate.cab_service.repository.FareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FareService {
    @Autowired
    private FareRepository fareRepository;

    public Fare calculateFare(double distance, double time, String vehicleType, boolean isSurge) throws IOException {
        FareCalculator calculator = isSurge ? new SurgeFare(1.5) : new StandardFare();
        double amount = calculator.calculateFare(distance, time);
        Fare fare = new Fare(null, distance, time, vehicleType, amount);
        return fareRepository.save(fare);
    }

    public Fare getFare(String fareId) throws IOException {
        return fareRepository.findById(fareId);
    }

    public List<Fare> getAllFares() throws IOException {
        return fareRepository.findAll();
    }
    public Fare applyDiscount(String fareId, double discount) throws IOException {
        Fare fare = fareRepository.findById(fareId);
        if (fare != null) {
            fare.setDiscount(discount);
            fare.setAmount(fare.getAmount() - discount);
            fareRepository.update(fare);
        }
        return fare;
    }
}