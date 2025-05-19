package com.taxi.user.controller;

import com.taxi.admin.model.AdminUser;
import com.taxi.user.model.Passenger;
import com.taxi.user.model.User;
import com.taxi.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService = new UserService();

    // ✅ Render Passenger Register Page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        return "user/register"; // templates/user/register.html
    }

    // ✅ Register Passenger
    @PostMapping("/register/passenger")
    public String registerPassenger(@RequestParam String username,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String phone,
                                    @RequestParam(required = false) String fullname, // Make optional
                                    @RequestParam(required = false) String address,
                                    RedirectAttributes redirectAttributes) {
        
        fullname = fullname != null ? fullname : "";
        address = address != null ? address : "";

        boolean isRegistered = userService.registerPassenger(username, email, password, phone, fullname, address);

        if (!isRegistered) {
            redirectAttributes.addFlashAttribute("error", "Username already exists! Please choose a different one.");
            return "redirect:/user/register";
        }

        redirectAttributes.addFlashAttribute("success", "Passenger registered successfully! Please login.");
        return "redirect:/user/login";
    }

    // ✅ Render Login Page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "user/login"; // templates/user/login.html
    }

    // ✅ Login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User loggedInUser = userService.validateCredentials(username, password);

        if (loggedInUser != null) {
            session.setAttribute("loggedInUser", loggedInUser);
            return "redirect:/user/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password!");
            return "redirect:/user/login";
        }
    }

    // ✅ User Dashboard
    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("name", loggedInUser.getUsername());
        return "user/user_dashboard"; // templates/user/user_dashboard.html
    }

    // ✅ Show User Profile
    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        if (loggedInUser instanceof Passenger) {
        Passenger passenger = (Passenger) loggedInUser;
        if (passenger.getFullname() == null) passenger.setFullname("");
        if (passenger.getAddress() == null) passenger.setAddress("");
    }

        model.addAttribute("user", loggedInUser);
        return "user/profile"; // templates/user/profile.html
    }

    // ✅ Update Profile
    @PostMapping("/update")
public String updateProfile(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String phone,
                          @RequestParam(required = false) String fullname,
                          @RequestParam(required = false) String address,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

    User loggedInUser = (User) session.getAttribute("loggedInUser");

    if (loggedInUser == null) {
        return "redirect:/user/login";
    }

    // Update common user fields
    loggedInUser.setUsername(username);
    loggedInUser.setEmail(email);

    // Update password if provided
    if (!password.isBlank()) {
        loggedInUser.setPassword(password);
    }

    // Update passenger-specific fields
    if (loggedInUser instanceof Passenger) {
        Passenger passenger = (Passenger) loggedInUser;
        passenger.setPhone(phone);
        passenger.setFullname(fullname);
        passenger.setAddress(address);
    }

    // Save updated user
    userService.updateUser(loggedInUser);
    
    // Update session with modified user object
    session.setAttribute("loggedInUser", loggedInUser);
    
    // Add success message
    redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");

    return "redirect:/user/profile";
}

    // ✅ Get All Users (for Admin View)
    @GetMapping("/all")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ Delete Passenger (Admin Permission Required)
    @PostMapping("/admin/delete/{username}")
    public String deletePassenger(@PathVariable String username,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {

        AdminUser loggedInUser = (AdminUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/admin/login";
        }

        String permission = loggedInUser.getPermission();
        if (!"all".equalsIgnoreCase(permission.trim()) && !"update".equalsIgnoreCase(permission.trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to delete passengers.");
            return "redirect:/admin/passengers";
        }

        boolean deleted = userService.deleteUserByUsername(username);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Passenger deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Passenger not found!");
        }

        return "redirect:/admin/passengers";
    }
}
