package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Singleton class for managing user data persistence.
 * Week 3 - Singleton Pattern: Ensures single instance for user data access.
 * Week 1 - Encapsulation: Encapsulates file I/O operations for user data.
 */
public class UserDatabase {
    private static UserDatabase instance;
    
    // Get the user file path - uses data folder for all user data
    private static String getFilePath() {
        String projectRoot = System.getProperty("user.dir");
        File dataDir = new File(projectRoot, "data");
        
        // Create data directory if it doesn't exist
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Created data directory: " + dataDir.getAbsolutePath());
        }
        
        // Check for existing user.txt in various locations and migrate to data folder
        File oldFile1 = new File(projectRoot, "user.txt");
        File oldFile2 = new File(projectRoot, "src/main/resources/Document/user.txt");
        File newFile = new File(dataDir, "user.txt");
        
        // Migrate existing files to data folder
        if (oldFile1.exists() && !newFile.exists()) {
            try {
                java.nio.file.Files.move(oldFile1.toPath(), newFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Migrated user.txt from project root to data folder");
            } catch (IOException e) {
                System.err.println("Error migrating user.txt: " + e.getMessage());
            }
        } else if (oldFile2.exists() && !newFile.exists()) {
            try {
                java.nio.file.Files.move(oldFile2.toPath(), newFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Migrated user.txt from resources to data folder");
            } catch (IOException e) {
                System.err.println("Error migrating user.txt: " + e.getMessage());
            }
        }
        
        // Return the data folder path
        return newFile.getAbsolutePath();
    }
    
    private static String FILE_NAME = null;
    
    /**
     * Private constructor for singleton pattern
     */
    private UserDatabase() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get singleton instance
     * Week 3 - Singleton Pattern: Thread-safe singleton implementation
     */
    public static synchronized UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }
    
    private static String getFileName() {
        if (FILE_NAME == null) {
            FILE_NAME = getFilePath();
        }
        return FILE_NAME;
    }

    // Method to get user details by username
    public Map<String, String> getUser(String username) {
        Map<String, Map<String, String>> users = readAllUsers();

        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null; // If user is not found, return null
    }

    // Add a new user
    public boolean addUser(String username, String password, String fullName, String phone, String email, String age) {
        return addUser(username, password, fullName, phone, email, age, "", "");
    }

    // Add a new user with security question
    public boolean addUser(String username, String password, String fullName, String phone, String email, String age, String securityQuestion, String securityAnswer) {
        Map<String, Map<String, String>> users = readAllUsers();
        if (users.containsKey(username)) {
            System.out.println("Error: Username already exists.");
            return false; // username exists
        }

        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);
        user.put("fullName", fullName);
        user.put("phone", phone);
        user.put("email", email);
        user.put("age", age);
        user.put("securityQuestion", securityQuestion);
        user.put("securityAnswer", securityAnswer.toLowerCase()); // Store in lowercase for case-insensitive comparison

        users.put(username, user);
        System.out.println("User added: " + username); // Debugging line
        return writeAllUsers(users);
    }

    // Update password for a given username
    public boolean updatePasswordByUsername(String username, String newPassword) {
        Map<String, Map<String, String>> users = readAllUsers();

        if (users.containsKey(username)) {
            // Update the password
            users.get(username).put("password", newPassword);
            // Save the updated users list back to the file
            return writeAllUsers(users);
        }

        return false; // Return false if username not found
    }

    // Update password by email
    public boolean updatePasswordByEmail(String email, String newPassword) {
        Map<String, Map<String, String>> users = readAllUsers();

        // Search for the user by email
        for (Map.Entry<String, Map<String, String>> entry : users.entrySet()) {
            if (entry.getValue().get("email").equals(email)) {
                // Update the password
                entry.getValue().put("password", newPassword);
                // Save the updated users list back to the file
                return writeAllUsers(users);
            }
        }

        return false; // Return false if no user with the given email is found
    }


    // Get user by email
    public Map<String, String> getUserByEmail(String email) {
        Map<String, Map<String, String>> users = readAllUsers();

        // Search for the user by email
        for (Map.Entry<String, Map<String, String>> entry : users.entrySet()) {
            if (entry.getValue().get("email").equals(email)) {
                return entry.getValue(); // Return user details if email matches
            }
        }

        return null; // Return null if no user with the given email is found
    }


    // Read all users from file
    public Map<String, Map<String, String>> readAllUsers() {
        checkFileExists(); // Ensure the file exists before reading it
        Map<String, Map<String, String>> users = new HashMap<>();
        File file = new File(getFileName());

        if (!file.exists()) {
            System.out.println("Warning: User file not found. Returning empty user list.");
            return users; // Return empty map if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",", -1); // Use -1 to preserve trailing empty fields
                if (parts.length < 6) {
                    System.out.println("Skipping invalid line (not enough fields): " + line);
                    continue; // skip invalid lines
                }

                Map<String, String> user = new HashMap<>();
                user.put("username", parts[0] != null ? parts[0].trim() : "");
                user.put("password", parts[1] != null ? parts[1].trim() : "");
                user.put("fullName", parts[2] != null ? parts[2].trim() : "");
                user.put("phone", parts[3] != null ? parts[3].trim() : "");
                
                // Handle email - check if it looks like it was split (contains @ but no .com/.edu/etc)
                String email = parts[4] != null ? parts[4].trim() : "";
                String potentialEmailPart = parts.length > 5 ? parts[5].trim() : "";
                
                // If email contains @ but the next part looks like a domain (contains .), combine them
                // Also check if the next part is NOT a number (age should be numeric)
                if (email.contains("@") && !email.contains(".") && 
                    potentialEmailPart.contains(".") && 
                    !potentialEmailPart.matches("\\d+") &&
                    !potentialEmailPart.equalsIgnoreCase("Beginner") &&
                    !potentialEmailPart.equalsIgnoreCase("Intermediate") &&
                    !potentialEmailPart.equalsIgnoreCase("Advanced")) {
                    // This looks like email was split - combine them
                    email = email + "@" + potentialEmailPart; // Combine with @ since it was split
                    // Age is missing, try to get from next field if available
                    if (parts.length >= 7) {
                        user.put("age", parts[6] != null ? parts[6].trim() : "");
                        user.put("level", parts.length > 7 && parts[7] != null && !parts[7].trim().isEmpty() ? parts[7].trim() : "Beginner");
                    } else if (parts.length == 6) {
                        // Only 6 parts, so level is at index 5 (which was the email domain)
                        user.put("age", ""); // Age is missing
                        user.put("level", parts[5] != null && !parts[5].trim().isEmpty() ? parts[5].trim() : "Beginner");
                    } else {
                        user.put("age", "");
                        user.put("level", "Beginner");
                    }
                } else {
                    // Normal parsing
                    // Age is at index 5, but if level exists, age might be at 5 and level at 6
                    if (parts.length >= 7) {
                        // Has level field - check if parts[5] is actually a level name (corrupted data)
                        String potentialAge = parts[5] != null ? parts[5].trim() : "";
                        String potentialLevel = parts[6] != null ? parts[6].trim() : "";
                        
                        // If parts[5] is a level name, then age is missing
                        if (potentialAge.equalsIgnoreCase("Beginner") || 
                            potentialAge.equalsIgnoreCase("Intermediate") || 
                            potentialAge.equalsIgnoreCase("Advanced")) {
                            // parts[5] is actually the level, age is missing
                            user.put("age", "");
                            user.put("level", potentialAge);
                        } else {
                            // Normal case: parts[5] is age, parts[6] is level
                            user.put("age", potentialAge);
                            user.put("level", potentialLevel.isEmpty() ? "Beginner" : potentialLevel);
                        }
                    } else if (parts.length == 6) {
                        // No level field, age is at 5
                        String potentialAge = parts[5] != null ? parts[5].trim() : "";
                        // Check if it's actually a level name (corrupted data)
                        if (potentialAge.equalsIgnoreCase("Beginner") || 
                            potentialAge.equalsIgnoreCase("Intermediate") || 
                            potentialAge.equalsIgnoreCase("Advanced")) {
                            // parts[5] is actually the level, age is missing
                            user.put("age", "");
                            user.put("level", potentialAge);
                        } else {
                            // Normal case: parts[5] is age
                            user.put("age", potentialAge);
                            user.put("level", "Beginner");
                        }
                    } else {
                        // Less than 6 fields, skip
                        System.out.println("Skipping invalid line (format error): " + line);
                        continue;
                    }
                }
                
                user.put("email", email);
                
                // Security question and answer (optional, for backward compatibility)
                if (parts.length >= 9) {
                    user.put("securityQuestion", parts[7] != null ? parts[7].trim() : "");
                    user.put("securityAnswer", parts[8] != null ? parts[8].trim() : "");
                } else {
                    user.put("securityQuestion", "");
                    user.put("securityAnswer", "");
                }

                users.put(user.get("username"), user); // Store by username (as primary key)
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    // Write all users to file
    public boolean writeAllUsers(Map<String, Map<String, String>> users) {
        Path path = Paths.get(getFileName());
        try {
            Files.createDirectories(path.getParent()); // Create parent directories if they don't exist
        } catch (IOException e) {
            System.out.println("Error creating directories: " + e.getMessage());
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFileName()))) {
            for (Map<String, String> user : users.values()) {
                // Use getOrDefault to handle null values and ensure proper field order
                String username = user.getOrDefault("username", "");
                String password = user.getOrDefault("password", "");
                String fullName = user.getOrDefault("fullName", "");
                String phone = user.getOrDefault("phone", "");
                String email = user.getOrDefault("email", "");
                String age = user.getOrDefault("age", "");
                String level = user.getOrDefault("level", "Beginner");
                
                // Join with commas - ensure no null values
                String userData = String.join(",",
                        username != null ? username : "",
                        password != null ? password : "",
                        fullName != null ? fullName : "",
                        phone != null ? phone : "",
                        email != null ? email : "",
                        age != null ? age : "",
                        level != null ? level : "Beginner");
                bw.write(userData);
                bw.newLine();
            }
            bw.flush(); // Ensure data is written
            System.out.println("User data written to file."); // Debugging line
            return true;
        } catch (IOException e) {
            System.out.println("Error writing user data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to check if the user file exists, and create it if not
    private static void checkFileExists() {
        File file = new File(getFileName());
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Create parent directories if they don't exist
            System.out.println("Directory created: " + parentDir.getPath());
        }
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath()); // Create the file if it doesn't exist
                System.out.println("User file created successfully.");
            } catch (IOException e) {
                System.out.println("Error creating user file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
