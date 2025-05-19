package com.taxi.user.service;

import com.taxi.user.model.Passenger;
import com.taxi.user.model.User;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
@Service
public class UserService {
    private static final String USERS_FILE = "users.txt";
    private static final String DELIMITER = "|"; // Using pipe delimiter for better handling

    // ✅ Updated Register Passenger method with new fields
    public boolean registerPassenger(String username, String email, String password, 
                                   String phone, String fullName, String address) {
        List<User> users = getAllUsers();

        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return false; // Username already taken
            }
        }

        Passenger newPassenger = new Passenger(username, email, password, phone, 
                                             fullName, address, false);
        saveUserToFile(newPassenger);
        return true;
    }

    // ✅ Save a new user to file
    private void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            if (user instanceof Passenger) {
                Passenger passenger = (Passenger) user;
                writer.write(String.join(DELIMITER,
                        "PASSENGER",
                        passenger.getUsername(),
                        passenger.getEmail(),
                        passenger.getPassword(),
                        passenger.getPhone(),
                        passenger.getFullname(),
                        passenger.getAddress(),
                        String.valueOf(passenger.isVerified())));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Read all users from file
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 5 && parts[0].equalsIgnoreCase("PASSENGER")) {
                    Passenger passenger = new Passenger(
                            parts[1], // username
                            parts[2], // email
                            parts[3], // password
                            parts[4], // phone
                            parts.length > 5 ? parts[5] : "", // fullName
                            parts.length > 6 ? parts[6] : "", // address
                            parts.length > 7 ? Boolean.parseBoolean(parts[7]) : false); // verified
                    users.add(passenger);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ✅ Return only passenger users
    public List<Passenger> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (user instanceof Passenger) {
                passengers.add((Passenger) user);
            }
        }
        return passengers;
    }

    // ✅ Login validation by username/email + password
    public User validateCredentials(String usernameOrEmail, String password) {
        for (User user : getAllUsers()) {
            if ((user.getUsername().equalsIgnoreCase(usernameOrEmail) ||
                 user.getEmail().equalsIgnoreCase(usernameOrEmail)) &&
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // ✅ Delete passenger by username
    public boolean deleteUserByUsername(String username) {
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(user -> user.getUsername().equalsIgnoreCase(username));
        if (removed) {
            saveAllUsers(users);
            return true;
        }
        return false;
    }

    // ✅ Save updated user list back to file
    private void saveAllUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                if (user instanceof Passenger) {
                    Passenger p = (Passenger) user;
                    writer.write(String.join(DELIMITER,
                            "PASSENGER",
                            p.getUsername(),
                            p.getEmail(),
                            p.getPassword(),
                            p.getPhone(),
                            p.getFullname(),
                            p.getAddress(),
                            String.valueOf(p.isVerified())));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Update user information
    public void updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                break;
            }
        }
        
        saveAllUsers(users);
    }
}