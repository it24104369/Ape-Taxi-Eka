package com.ridebooking.controller;

import com.ridebooking.model.Driver;
import com.ridebooking.model.Review;
import com.ridebooking.model.Ride;
import com.ridebooking.service.DriverService;
import com.ridebooking.service.ReviewService;
import com.ridebooking.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String getAllRides(Model model) {
        model.addAttribute("rides", rideService.getAllRides());
        return "ride/list";
    }

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("ride", new Ride());
        return "ride/request";
    }

    @PostMapping("/request")
    public String requestRide(@ModelAttribute Ride ride, RedirectAttributes redirectAttributes) {
        Ride createdRide = rideService.createRide(ride);
        if (createdRide != null) {
            redirectAttributes.addFlashAttribute("message", "Ride requested successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to request ride!");
        }
        return "redirect:/rides/status";
    }

    @GetMapping("/status")
    public String showRideStatus(Model model) {
        // Get all rides
        List<Ride> allRides = rideService.getAllRides();

        // Log the number of rides found
        System.out.println("Found " + allRides.size() + " total rides");

        // Filter for active rides (not completed or cancelled)
        List<Ride> activeRides = allRides.stream()
                .filter(ride -> !"COMPLETED".equals(ride.getStatus()) && !"CANCELLED".equals(ride.getStatus()))
                .collect(Collectors.toList());

        System.out.println("Filtered to " + activeRides.size() + " active rides");

        // Debug log each active ride
        for (Ride ride : activeRides) {
            System.out.println("Active ride: ID=" + ride.getId() + ", Status=" + ride.getStatus());
        }

        model.addAttribute("rides", activeRides);
        return "ride/status";
    }

    @GetMapping("/history")
    public String showRideHistory(Model model) {
        // Get all rides
        List<Ride> allRides = rideService.getAllRides();

        // Filter for completed or cancelled rides
        List<Ride> historyRides = allRides.stream()
                .filter(ride -> "COMPLETED".equals(ride.getStatus()) || "CANCELLED".equals(ride.getStatus()))
                .collect(Collectors.toList());

        // Debug log
        System.out.println("Total rides: " + allRides.size());
        System.out.println("History rides: " + historyRides.size());
        for (Ride ride : historyRides) {
            System.out.println("Ride ID: " + ride.getId() + ", Status: " + ride.getStatus());
        }

        // Add all history rides to the model
        model.addAttribute("rides", historyRides);
        return "ride/history";
    }

    @GetMapping("/assignments")
    public String showDriverAssignments(Model model) {
        model.addAttribute("rides", rideService.getRidesByStatus("REQUESTED"));
        model.addAttribute("availableDrivers", driverService.getAvailableDrivers());
        return "ride/assignments";
    }

    @PostMapping("/assign")
    public String assignDriver(@RequestParam String rideId, @RequestParam String driverId,
                               RedirectAttributes redirectAttributes) {
        System.out.println("Assigning driver ID: " + driverId + " to ride ID: " + rideId);

        Ride updatedRide = rideService.assignDriverToRide(rideId, driverId);
        if (updatedRide != null) {
            redirectAttributes.addFlashAttribute("message", "Driver assigned successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to assign driver! Ride or driver not found.");
        }
        return "redirect:/rides/assignments";
    }

    @GetMapping("/update-status/{id}")
    public String showUpdateStatusForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("Showing update status form for ride: " + id);

        Optional<Ride> optionalRide = rideService.getRideById(id);
        if (optionalRide.isPresent()) {
            Ride ride = optionalRide.get();
            model.addAttribute("ride", ride);

            // Create a map of available statuses based on current status
            Map<String, String> availableStatuses = new LinkedHashMap<>();

            // Logic for status progression
            switch (ride.getStatus()) {
                case "REQUESTED":
                    availableStatuses.put("ACCEPTED", "Accept Ride");
                    availableStatuses.put("CANCELLED", "Cancel Ride");
                    break;
                case "ACCEPTED":
                    availableStatuses.put("IN_PROGRESS", "Start Ride");
                    availableStatuses.put("CANCELLED", "Cancel Ride");
                    break;
                case "IN_PROGRESS":
                    availableStatuses.put("COMPLETED", "Complete Ride");
                    break;
                default:
                    // No status changes available
                    break;
            }

            model.addAttribute("availableStatuses", availableStatuses);

            // Debug log to verify the ride is being retrieved correctly
            System.out.println("Retrieved ride for update: " + ride.getId() + ", Status: " + ride.getStatus());
            return "ride/update-status";
        } else {
            System.out.println("Ride not found with ID: " + id);
            redirectAttributes.addFlashAttribute("error", "Ride not found!");
            return "redirect:/rides/status";
        }
    }

    @PostMapping("/update-status")
    public String updateRideStatus(@RequestParam String rideId, @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {
        System.out.println("Processing update status form for ride: " + rideId + " to status: " + status);

        Ride ride = rideService.getRideById(rideId).orElse(null);
        if (ride != null) {
            // Debug log the current state
            System.out.println("Found ride to update: " + ride.getId() + ", current status: " + ride.getStatus());

            // Call service to update the status
            Ride updatedRide = rideService.updateRideStatus(rideId, status);

            if (updatedRide != null) {
                redirectAttributes.addFlashAttribute("message", "Ride status updated successfully to " + status + "!");

                // Redirect to history page if the ride is completed or cancelled
                if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
                    return "redirect:/rides/history";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update ride status!");
            }
        } else {
            System.out.println("Failed to find ride with ID: " + rideId);
            redirectAttributes.addFlashAttribute("error", "Ride not found!");
        }

        return "redirect:/rides/status";
    }

    @GetMapping("/cancel/{id}")
    public String cancelRide(@PathVariable String id, RedirectAttributes redirectAttributes) {
        System.out.println("Cancelling ride: " + id);

        Ride updatedRide = rideService.updateRideStatus(id, "CANCELLED");

        if (updatedRide != null) {
            redirectAttributes.addFlashAttribute("message", "Ride cancelled successfully!");
            return "redirect:/rides/history";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel ride! Ride not found.");
            return "redirect:/rides/status";
        }
    }

    // For debugging purposes
    @GetMapping("/debug")
    @ResponseBody
    public String debugRides() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>All Rides</h1>");

        List<Ride> allRides = rideService.getAllRides();
        sb.append("<p>Total rides: ").append(allRides.size()).append("</p>");

        for (Ride ride : allRides) {
            sb.append("<p>")
                    .append("ID: ").append(ride.getId())
                    .append(", Status: ").append(ride.getStatus())
                    .append(", Passenger: ").append(ride.getPassengerName())
                    .append(", Driver ID: ").append(ride.getDriverId() != null ? ride.getDriverId() : "None")
                    .append("</p>");
        }

        return sb.toString();
    }
}