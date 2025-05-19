package com.RideMate.cab_service.repository;
import com.RideMate.cab_service.model.Payment;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PaymentRepository {
    private static final String FILE_PATH = "data/payments.txt";

    public Payment save(Payment payment) throws IOException {
        payment.setPaymentId(UUID.randomUUID().toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(payment.getPaymentId() + "," + payment.getFareId() + "," + payment.getAmount() + "," +
                       payment.getPaymentMethod() + "," + payment.getStatus());
            writer.newLine();
        }
        return payment;
    }
    public Payment findById(String paymentId) throws IOException {
        List<Payment> payments = findAll();
        return payments.stream().filter(p -> p.getPaymentId().equals(paymentId)).findFirst().orElse(null);
    }

    public List<Payment> findAll() throws IOException {
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Payment payment = new Payment(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]);
                payments.add(payment);
            }
        }
        return payments;
    }
    public Payment update(Payment payment) throws IOException {
        List<Payment> payments = findAll();
        payments.removeIf(p -> p.getPaymentId().equals(payment.getPaymentId()));
        payments.add(payment);
        saveAll(payments);
        return payment;
    }

    private void saveAll(List<Payment> payments) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Payment payment : payments) {
                writer.write(payment.getPaymentId() + "," + payment.getFareId() + "," + payment.getAmount() + "," +
                           payment.getPaymentMethod() + "," + payment.getStatus());
                writer.newLine();
            }
        }
    }
}