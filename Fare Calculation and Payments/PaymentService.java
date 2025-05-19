package com.RideMate.cab_service.service;

import com.RideMate.cab_service.model.*;
import com.RideMate.cab_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.RideMate.cab_service.model.CashPayment; // Update to the correct package where CashPayment exists

import java.io.IOException;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(String fareId, double amount, String paymentMethod) throws IOException {
        PaymentProcessor processor;
        switch (paymentMethod) {
            case "cash":
                processor = new CashPayment();
                break;
            case "credit_card":
                processor = new CreditCardPayment();
                break;
            case "digital_wallet":
                processor = new DigitalWalletPayment();
                break;
            default:
                throw new IllegalArgumentException("Invalid payment method");
        }

        boolean success = processor.processPayment(amount);
        if (!success) {
            throw new RuntimeException("Payment processing failed");
        }

        Payment payment = new Payment(null, fareId, amount, paymentMethod, "completed");
        return paymentRepository.save(payment);
    }

    public Payment getPayment(String paymentId) throws IOException {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getAllPayments() throws IOException {
        return paymentRepository.findAll();
    }

    public Payment refundPayment(String paymentId) throws IOException {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment != null) {
            payment.setStatus("refunded");
            paymentRepository.update(payment);
        }
        return payment;
    }
}