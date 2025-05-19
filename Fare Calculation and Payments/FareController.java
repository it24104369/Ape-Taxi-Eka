package com.RideMate.cab_service.controller;

import com.RideMate.cab_service.model.Fare;
import com.RideMate.cab_service.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/fare")
public class FareController {
    @Autowired
    private FareService fareService;

    @GetMapping("/estimate")
    public String showEstimateForm() {
        return "fare_estimation";
    }

    @PostMapping("/estimate")
    public String calculateFare(@RequestParam double distance, @RequestParam double time,
                               @RequestParam String vehicleType, @RequestParam(defaultValue = "false") boolean isSurge,
                               Model model) throws IOException {
        Fare fare = fareService.calculateFare(distance, time, vehicleType, isSurge);
        model.addAttribute("fare", fare);
        return "fare_estimation";
    }
    @GetMapping("/details/{fareId}")
    public String getFareDetails(@PathVariable String fareId, Model model) throws IOException {
        Fare fare = fareService.getFare(fareId);
        model.addAttribute("fare", fare);
        return "fare_details";
    }

    @PostMapping("/discount/{fareId}")
    public String applyDiscount(@PathVariable String fareId, @RequestParam double discount,
                               Model model) throws IOException {
        Fare fare = fareService.applyDiscount(fareId, discount);
        model.addAttribute("fare", fare);
        return "fare_details";
    }
}
