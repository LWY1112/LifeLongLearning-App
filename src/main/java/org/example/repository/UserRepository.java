package org.example.repository;

import org.example.UserDatabase;

import java.util.Map;

/**
 * Repository implementation for user data access using file-based storage.
 * Week 3 - Singleton: Delegates to UserDatabase singleton.
 * Week 3 - Decoupling: Implements IUserRepository interface for loose coupling.
 */
public class UserRepository implements IUserRepository {
    private static UserRepository instance;
    private final UserDatabase userDatabase;

    // Private constructor for singleton pattern
    private UserRepository() {
        this.userDatabase = UserDatabase.getInstance();
    }

    /**
     * Get singleton instance
     * Week 3 - Singleton Pattern: Ensures single instance of repository
     */
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    @Override
    public Map<String, String> getUser(String username) {
        return userDatabase.getUser(username);
    }

    @Override
    public Map<String, String> getUserByEmail(String email) {
        return userDatabase.getUserByEmail(email);
    }

    @Override
    public boolean addUser(String username, String password, String fullName, String phone, String email, String age) {
        return userDatabase.addUser(username, password, fullName, phone, email, age);
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        return userDatabase.updatePasswordByUsername(username, newPassword);
    }

    @Override
    public Map<String, Map<String, String>> readAllUsers() {
        return userDatabase.readAllUsers();
    }

    @Override
    public boolean writeAllUsers(Map<String, Map<String, String>> users) {
        return userDatabase.writeAllUsers(users);
    }
}

