package test;

import org.example.repository.IUserRepository;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of IUserRepository for testing.
 */
public class MockUserRepository implements IUserRepository {
    private Map<String, Map<String, String>> users = new HashMap<>();

    public MockUserRepository() {
        // Add a default test user
        Map<String, String> testUser = new HashMap<>();
        testUser.put("username", "testuser");
        testUser.put("password", "password123");
        testUser.put("fullName", "Test User");
        testUser.put("phone", "1234567890");
        testUser.put("email", "test@example.com");
        testUser.put("age", "25");
        testUser.put("level", "Beginner");
        users.put("testuser", testUser);
    }

    @Override
    public Map<String, String> getUser(String username) {
        return users.get(username);
    }

    @Override
    public Map<String, Map<String, String>> readAllUsers() {
        return new HashMap<>(users);
    }

    @Override
    public boolean writeAllUsers(Map<String, Map<String, String>> users) {
        this.users = new HashMap<>(users);
        return true;
    }

    @Override
    public boolean addUser(String username, String password, String fullName, String phone, String email, String age) {
        if (users.containsKey(username)) {
            return false;
        }
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", password);
        userData.put("fullName", fullName);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("age", age);
        userData.put("level", "Beginner");
        users.put(username, userData);
        return true;
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        Map<String, String> user = users.get(username);
        if (user != null) {
            user.put("password", newPassword);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> getUserByEmail(String email) {
        for (Map<String, String> user : users.values()) {
            if (user.get("email") != null && user.get("email").equals(email)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(String username, Map<String, String> userData) {
        users.put(username, userData);
    }
}


