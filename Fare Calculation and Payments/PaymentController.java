package com.RideMate.cab_service.controller;

import com.RideMate.cab_service.model.Payment;
import com.RideMate.cab_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    // Show payment form
    @GetMapping("/process/{fareId}")
    public String showPaymentForm(@PathVariable String fareId, Model model) {
        model.addAttribute("fareId", fareId);
        return "payment"; // Render payment.jsp
    }

    // Process payment and show confirmation
    @PostMapping("/process")
    public String processPayment(@RequestParam String fareId,
                                @RequestParam double amount,
                                @RequestParam String paymentMethod,
                                @RequestParam(required = false) String cardNumber,
                                @RequestParam(required = false) String expiryDate,
                                @RequestParam(required = false) String cvv,
                                Model model) throws IOException {
        // Validate credit card details if payment method is credit_card
        if (paymentMethod.equals("credit_card")) {
            if (cardNumber == null || cardNumber.isEmpty() ||
                expiryDate == null || expiryDate.isEmpty() ||
                cvv == null || cvv.isEmpty()) {
                model.addAttribute("error", "Please provide all credit card details.");
                model.addAttribute("fareId", fareId);
                return "payment"; // Return to payment.jsp with error
            }
            // Simulate card validation (e.g., check format)
            if (!cardNumber.matches("\\d{16}")) { // Example: 16-digit card number
                model.addAttribute("error", "Invalid card number.");
                model.addAttribute("fareId", fareId);
                return "payment";
            }
        }

        try {
            Payment payment = paymentService.processPayment(fareId, amount, paymentMethod);
            model.addAttribute("payment", payment);
            return "payment_confirmation"; // Render payment_confirmation.jsp
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("fareId", fareId);
            return "payment"; // Return to payment.jsp with error
        }
    }

    // View payment history
    @GetMapping("/history")
    public String getPaymentHistory(Model model) throws IOException {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payment_history";
    }

    // Refund payment
    @PostMapping("/refund/{paymentId}")
    public String refundPayment(@PathVariable String paymentId, Model model) throws IOException {
        Payment payment = paymentService.refundPayment(paymentId);
        model.addAttribute("payment", payment);
        return "payment_confirmation";
    }

    @GetMapping
public String defaultPaymentPage() {
    return "redirect:/fare/estimate"; // Redirect to fare estimation page
}

}