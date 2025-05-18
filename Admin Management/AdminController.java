package com.taxi.admin.controller;

import com.taxi.admin.model.AdminUser;
import com.taxi.admin.util.FileUtil;
import com.taxi.driver.model.Driver;
import com.taxi.driver.service.DriverService;
import com.taxi.review.model.Review;
import com.taxi.review.service.ReviewService;
import com.taxi.ride.model.Ride;
import com.taxi.ride.service.RideService;
import com.taxi.user.model.Passenger;
import com.taxi.user.service.UserService;
import com.taxi.user.util.UserFileUtil;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    private final String uploadDir = new File("src/main/resources/static/images/uploads").getAbsolutePath();

    @GetMapping("/admin/register")
    public String showRegistrationForm(@RequestParam(value = "source", required = false) String source,
                                       Model model) {
        model.addAttribute("source", source);
        return "admin/admin_register";
    }

    @PostMapping("/admin/save")
    public String registerAdmin(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email,
                                @RequestParam String role,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String phone,
                                @RequestParam("profilePicture") MultipartFile profilePicture,
                                @RequestParam String permission,
                                @RequestParam(value = "source", required = false) String source,
                                RedirectAttributes redirectAttributes) {
        try {
            if (FileUtil.isUsernametaken(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists. Please choose another.");
                return "redirect:/admin/register";
            }

            // Ensure upload directory exists
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            // Save profile image
            String fileName = username + "_" + profilePicture.getOriginalFilename();
            String filePath = uploadDir + File.separator + fileName;
            profilePicture.transferTo(new File(filePath));
            String relativePath = "/images/uploads/" + fileName;

            AdminUser admin = new AdminUser(username, password, email, role,
                    firstName, lastName, phone, relativePath, permission);

            FileUtil.writeToFile("admins.txt", admin.toFileFormat());
            FileUtil.logAdminActivity("Admin registered: " + admin.getUsername());

            redirectAttributes.addFlashAttribute("message", "Registration successful!");

            return "internal".equals(source) ? "redirect:/admin/list" : "redirect:/admin/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to register admin: " + e.getMessage());
            return "redirect:/admin/register";
        }
    }

    @GetMapping("/admin/list")
    public String listAdmins(Model model, HttpSession session) {
        List<String> admins = FileUtil.readAllAdmins();
        AdminUser loggedInUser = (AdminUser) session.getAttribute("loggedInUser");

        model.addAttribute("admins", admins);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("activePage", "list");
        model.addAttribute("profileImagePath", getProfileImagePath(loggedInUser));

        return "admin/admin_list";
    }

    @GetMapping("/admin/dashboard")
public String adminDashboard(Model model, HttpSession session) {
    AdminUser loggedInUser = (AdminUser) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        return "redirect:/admin/login";
    }

    // Get total admins from file
    int totalAdmins = FileUtil.readAllAdmins().size();
    int totalPassengers = UserFileUtil.readAllUsers().size();
    int totalDrivers = driverService.getAllDrivers().size();

    Map<String, String> stats = new HashMap<>();
    stats.put("totalAdmins", String.valueOf(totalAdmins));
    stats.put("totalPassengers", String.valueOf(totalPassengers));
    stats.put("totalDrivers", String.valueOf(totalDrivers));


    model.addAttribute("stats", stats);
    model.addAttribute("loggedInUser", loggedInUser);
    model.addAttribute("activePage", "dashboard");
    model.addAttribute("profileImagePath", getProfileImagePath(loggedInUser));

    return "admin/admin_dashboard";
}



    @GetMapping("/admin/edit/{username}")
    public String showEditForm(@PathVariable String username, Model model) {
        AdminUser admin = FileUtil.getAdminByUsername(username);
        if (admin == null) {
            throw new RuntimeException("Admin not found: " + username);
        }
        model.addAttribute("admin", admin);
        return "admin/admin_edit";
    }

    @PostMapping("/admin/update")
    public String updateAdmin(@ModelAttribute AdminUser updatedAdmin,
                              @RequestParam("profilePicture") MultipartFile profilePicture,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!profilePicture.isEmpty()) {
                String fileName = updatedAdmin.getUsername() + "_" + profilePicture.getOriginalFilename();
                String filePath = uploadDir + File.separator + fileName;
                profilePicture.transferTo(new File(filePath));
                updatedAdmin.setProfilePicturePath("/images/uploads/" + fileName);
            } else {
                AdminUser oldAdmin = FileUtil.getAdminByUsername(updatedAdmin.getUsername());
                if (oldAdmin != null) {
                    updatedAdmin.setProfilePicturePath(oldAdmin.getProfilePicturePath());
                }
            }

            FileUtil.updateAdmin(updatedAdmin);
            FileUtil.logAdminActivity("Admin updated: " + updatedAdmin.getUsername());
            redirectAttributes.addFlashAttribute("message", "Admin updated successfully!");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating admin: " + e.getMessage());
        }

        return "redirect:/admin/list";
    }

    @GetMapping("/admin/delete")
    public String deleteAdmin(@RequestParam String username) {
        FileUtil.deleteAdmin(username);
        FileUtil.logAdminActivity("Admin deleted: " + username);
        return "redirect:/admin/list";
    }

    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin/admin_login";
    }

    @PostMapping("/admin/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {
        AdminUser admin = FileUtil.getAdminByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            FileUtil.logAdminActivity("Admin logged in: " + username);
            session.setAttribute("loggedInUser", admin);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "admin/admin_login";
        }
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/profile")
    public String showAdminProfile(Model model, HttpSession session) {
        AdminUser loggedInUser = (AdminUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("profileImagePath", getProfileImagePath(loggedInUser));
        return "admin/admin_profile";
    }

    @Autowired
    private UserService userService;

    @GetMapping("/admin/passengers")
    public String getPassengerList(Model model, HttpSession session) {
    AdminUser loggedInUser = (AdminUser) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        return "redirect:/admin/login";
    }

    List<Passenger> passengers = userService.getAllPassengers();
    model.addAttribute("passengers", passengers);
    model.addAttribute("loggedInUser", loggedInUser);
    model.addAttribute("activePage", "passengers");
    model.addAttribute("profileImagePath", getProfileImagePath(loggedInUser));
    return "user/admin_passenger_list";
}


    private String getProfileImagePath(AdminUser user) {
        if (user != null && user.getProfilePicturePath() != null) {
            return user.getProfilePicturePath();
        }
        return "/images/default-avatar.png";
    }

    
    @Autowired
    private DriverService driverService;
    @GetMapping("/admin/drivers")
    public String showDriverList(Model model) {
        List<Driver> drivers = driverService.getAllDrivers();
        model.addAttribute("drivers", drivers);
        return "driver/list"; 
    }

    @Autowired
private ReviewService reviewService;

@GetMapping("/admin/reviews")
public String showAllReviews(Model model) {
    List<Review> pendingReviews = reviewService.getPendingReviews();
    List<Review> approvedReviews = reviewService.getApprovedReviews();

    model.addAttribute("pendingReviews", pendingReviews);
    model.addAttribute("approvedReviews", approvedReviews);

    return "review/admin_review"; // You will design this HTML to show both lists
}

@Autowired
private RideService rideService;

    @GetMapping("/admin/rides/assignments")
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

}

     
