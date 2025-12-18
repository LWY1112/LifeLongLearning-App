package org.example.repository;

import java.util.Map;

/**
 * Repository interface for user data access.
 * Week 3 - Decoupling: Interface for loose coupling and OCP.
 * Week 4 - Flexibility: Allows different implementations (File, Database, etc.)
 */
public interface IUserRepository {
    /**
     * Get user details by username
     * @param username Username to search for
     * @return User data map, or null if not found
     */
    Map<String, String> getUser(String username);

    /**
     * Get user details by email
     * @param email Email to search for
     * @return User data map, or null if not found
     */
    Map<String, String> getUserByEmail(String email);

    /**
     * Add a new user
     * @param username Username
     * @param password Password
     * @param fullName Full name
     * @param phone Phone number
     * @param email Email address
     * @param age Age
     * @return true if successful, false otherwise
     */
    boolean addUser(String username, String password, String fullName, String phone, String email, String age);

    /**
     * Update password for a user
     * @param username Username
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    boolean updatePassword(String username, String newPassword);

    /**
     * Read all users from storage
     * @return Map of username to user data
     */
    Map<String, Map<String, String>> readAllUsers();

    /**
     * Write all users to storage
     * @param users Map of username to user data
     * @return true if successful, false otherwise
     */
    boolean writeAllUsers(Map<String, Map<String, String>> users);
}

