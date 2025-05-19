package com.taxi.user.util;

import com.taxi.user.model.Passenger;
import com.taxi.user.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFileUtil {

    private static final String USER_FILE_PATH = "users.txt";  // Path to user data file
    private static final String DELIMITER = "|";  // Using pipe delimiter for better handling of addresses

    // Save user to file (handles Passenger with new fields)
    public static void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            StringBuilder line = new StringBuilder();

            // Common user fields
            line.append(escapeField(user.getUsername())).append(DELIMITER);
            line.append(escapeField(user.getEmail())).append(DELIMITER);
            line.append(escapeField(user.getPassword())).append(DELIMITER);

            // Passenger-specific fields
            if (user instanceof Passenger) {
                Passenger p = (Passenger) user;
                line.append(escapeField(p.getPhone())).append(DELIMITER);
                line.append(escapeField(p.getFullname())).append(DELIMITER);
                line.append(escapeField(p.getAddress())).append(DELIMITER);
                line.append("PASSENGER");
            } else {
                // For other user types, add placeholders
                line.append(DELIMITER); // phone
                line.append(DELIMITER); // fullName
                line.append(DELIMITER); // address
                line.append("UNKNOWN");
            }

            writer.write(line.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read all users from the file
    public static List<User> readAllUsers() {
    List<User> users = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\" + DELIMITER, -1);

            // Minimum required fields: username, email, password
            if (parts.length < 3) {
                System.err.println("Invalid user record at line " + lineNumber + ": insufficient fields");
                continue;
            }

            try {
                String username = unescapeField(parts[0].trim());
                String email = unescapeField(parts[1].trim());
                String password = unescapeField(parts[2].trim());
                String phone = parts.length > 3 ? unescapeField(parts[3].trim()) : "";
                
                // Default to PASSENGER if role isn't specified
                String role = "PASSENGER";
                if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                    role = unescapeField(parts[6].trim());
                }

                if ("PASSENGER".equalsIgnoreCase(role)) {
                    String fullName = parts.length > 4 ? unescapeField(parts[4].trim()) : "";
                    String address = parts.length > 5 ? unescapeField(parts[5].trim()) : "";
                    users.add(new Passenger(username, email, password, phone, fullName, address, false));
                }
            } catch (Exception e) {
                System.err.println("Error processing line " + lineNumber + ": " + e.getMessage());
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading user file: " + e.getMessage());
        e.printStackTrace();
    }

    return users;
}

    // Generate unique user ID using UUID
    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    // Escape field by replacing delimiter with special marker
    private static String escapeField(String field) {
        if (field == null) return "";
        return field.replace(DELIMITER, "\\" + DELIMITER)
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    // Unescape field
    private static String unescapeField(String field) {
        if (field == null || field.isEmpty()) return "";
        return field.replace("\\" + DELIMITER, DELIMITER)
                   .replace("\\n", "\n")
                   .replace("\\r", "\r");
    }
    
}