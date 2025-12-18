package org.example;

import org.example.model.CourseStatus;
import org.example.model.LearnerLevel;
import org.example.model.UserProfile;
import org.example.repository.IUserRepository;
import org.example.repository.UserRepository;
import org.example.service.LevelProgressionService;

import java.io.*;
import java.util.*;

/**
 * Core entity representing a learner/user.
 * Week 1 - Encapsulation: Proper encapsulation with private fields and controlled access.
 * Week 4 - Composition: Uses UserProfile and LevelProgressionService through composition.
 */
public class Learner implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String level;
    private UserProfile profile; // Week 4 - Composition: Using UserProfile value object
    private List<String> currentSkills = new ArrayList<>();
    private List<String> targetSkills = new ArrayList<>();
    private List<Course> enrolledCourses = new ArrayList<>();
    
    // Week 4 - Composition: Delegating level progression to service
    private transient LevelProgressionService levelProgressionService;

    /**
     * Constructor: initialize user info from UserRepository
     * Week 4 - Composition: Uses IUserRepository interface for dependency injection
     */
    public Learner(String username) {
        this(username, UserRepository.getInstance());
    }

    /**
     * Constructor with dependency injection for testing
     * Week 4 - Composition: Allows injection of dependencies
     */
    public Learner(String username, IUserRepository userRepository) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        Map<String, String> user = userRepository.getUser(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        this.username = username;
        this.password = user.get("password");
        this.level = user.getOrDefault("level", LearnerLevel.BEGINNER.getDisplayName());
        
        // Week 4 - Composition: Create UserProfile from user data
        // UserProfile constructor now handles null/empty values gracefully
        this.profile = new UserProfile(
            user.get("fullName"),
            user.get("phone"),
            user.get("email"),
            user.get("age")
        );

        this.levelProgressionService = new LevelProgressionService();
        
        // Load enrolled courses from a file after loading user data
        loadEnrolledCourses();
    }

    // Basic info getters/setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    // Week 4 - Composition: Delegate to UserProfile
    public String getFullName() {
        return profile != null ? profile.getFullName() : null;
    }

    public void setFullName(String fullName) {
        if (profile != null) {
            profile.setFullName(fullName);
        }
    }

    public String getPhone() {
        return profile != null ? profile.getPhone() : null;
    }

    public void setPhone(String phone) {
        if (profile != null) {
            profile.setPhone(phone);
        }
    }

    public String getEmail() {
        return profile != null ? profile.getEmail() : null;
    }

    public void setEmail(String email) {
        if (profile != null) {
            profile.setEmail(email);
        }
    }

    public String getAge() {
        return profile != null ? profile.getAge() : null;
    }

    public void setAge(String age) {
        if (profile != null) {
            profile.setAge(age);
        }
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = LearnerLevel.fromString(level).getDisplayName();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Skills management
    public List<String> getCurrentSkills() {
        return new ArrayList<>(currentSkills); // Defensive copy
    }

    public void addCurrentSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !currentSkills.contains(skill)) {
            currentSkills.add(skill.trim());
        }
    }

    public List<String> getTargetSkills() {
        return new ArrayList<>(targetSkills); // Defensive copy
    }

    public void addTargetSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty() && !targetSkills.contains(skill)) {
            targetSkills.add(skill.trim());
        }
    }

    // Course management
    public List<Course> getEnrolledCourses() {
        return new ArrayList<>(enrolledCourses); // Defensive copy
    }

    public void enroll(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (!isEnrolled(course)) {
            enrolledCourses.add(course);
            course.setStatus(CourseStatus.ENROLLED);
            saveEnrolledCourses();
        }
    }

    public boolean isEnrolled(Course c) {
        if (c == null) return false;
        return enrolledCourses.stream()
                .anyMatch(course -> course.getTitle().equals(c.getTitle()));
    }

    /**
     * Mark a course as completed
     * Week 4 - Composition: Delegates level progression to service
     */
    public void completeCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        for (Course c : enrolledCourses) {
            if (c.getTitle().equals(course.getTitle())) {
                c.setStatus(CourseStatus.COMPLETED);
                break;
            }
        }
        
        // Week 4 - Composition: Use level progression service
        if (levelProgressionService == null) {
            levelProgressionService = new LevelProgressionService();
        }
        levelProgressionService.checkAndUpgradeLevel(this);
        
        // Save updated level to database
        saveLevelToDatabase();
        // Save enrolled courses to persist the completion status
        saveEnrolledCourses();
    }

    /**
     * Get the current level with progress information
     * Week 4 - Composition: Delegates to LevelProgressionService
     */
    public String getLevelWithProgress() {
        if (levelProgressionService == null) {
            levelProgressionService = new LevelProgressionService();
        }
        return levelProgressionService.getLevelWithProgress(this);
    }

    /**
     * Save learner level to database
     */
    private void saveLevelToDatabase() {
        IUserRepository userRepository = UserRepository.getInstance();
        Map<String, Map<String, String>> users = userRepository.readAllUsers();
        if (users.containsKey(username)) {
            Map<String, String> user = users.get(username);
            user.put("level", level);
            userRepository.writeAllUsers(users);
        }
    }

    // Check if a course is completed
    public boolean isCompleted(Course c) {
        if (c == null) return false;
        return enrolledCourses.stream()
                .anyMatch(course -> course.getTitle().equals(c.getTitle()) &&
                        CourseStatus.COMPLETED.equals(course.getStatus()));
    }

    // Get number of completed courses per level
    public int getCompletedCount(String level) {
        if (level == null || level.isEmpty()) return 0;
        return (int) enrolledCourses.stream()
                .filter(c -> level.equalsIgnoreCase(c.getLevel()))
                .filter(c -> CourseStatus.COMPLETED.equals(c.getStatus()))
                .count();
    }

    /**
     * Check if learner can enroll in a course based on level requirements
     * Week 4 - Composition: Uses LevelProgressionService
     */
    public boolean canEnroll(Course c) {
        if (c == null) return false;
        
        if (isEnrolled(c)) return false;
        
        LearnerLevel courseLevel = LearnerLevel.fromString(c.getLevel());
        if (levelProgressionService == null) {
            levelProgressionService = new LevelProgressionService();
        }
        LearnerLevel allowedLevel = levelProgressionService.getAllowedLevel(this);
        
        return allowedLevel.canAccess(courseLevel);
    }

    /**
     * Get allowed level for UI (used to enable/disable enroll buttons)
     * Week 4 - Composition: Delegates to LevelProgressionService
     */
    public String getAllowedLevel(int requiredCoursesPerLevel) {
        if (levelProgressionService == null) {
            levelProgressionService = new LevelProgressionService();
        }
        LearnerLevel allowedLevel = levelProgressionService.getAllowedLevel(this);
        return allowedLevel.getDisplayName();
    }

    // Save learner back to database (including courses)
    public boolean saveToDatabase(String oldUsername) {
        IUserRepository userRepository = UserRepository.getInstance();
        Map<String, Map<String, String>> users = userRepository.readAllUsers();

        // Check if the new username already exists in the system
        if (!oldUsername.equals(this.username) && users.containsKey(this.username)) {
            System.out.println("Error: Username already exists.");
            return false;
        }

        // Update the user data
        Map<String, String> updatedUser = new HashMap<>();
        updatedUser.put("username", this.username);
        updatedUser.put("password", this.password);
        if (profile != null) {
            updatedUser.put("fullName", profile.getFullName());
            updatedUser.put("phone", profile.getPhone());
            updatedUser.put("email", profile.getEmail());
            updatedUser.put("age", profile.getAge());
        }
        updatedUser.put("level", this.level);

        // Remove the old entry and add the updated user
        users.put(this.username, updatedUser);
        if (!oldUsername.equals(this.username)) {
            users.remove(oldUsername);
        }

        // Save the enrolled courses as well
        saveEnrolledCourses();

        // Write the updated user list back to user.txt
        return userRepository.writeAllUsers(users);
    }

    // Save the enrolled courses to a file
    public void saveEnrolledCourses() {
        // Use data folder for course files
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        File courseFile = new File(dataDir, username + "_courses.dat");
        System.out.println("Saving enrolled courses to: " + courseFile.getAbsolutePath());

        if (!courseFile.exists()) {
            try {
                courseFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(courseFile))) {
            out.writeObject(enrolledCourses);
            System.out.println("Courses saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }

    // Load enrolled courses from file
    public void loadEnrolledCourses() {
        // Use data folder for course files
        File dataDir = new File("data");
        File courseFile = new File(dataDir, username + "_courses.dat");
        
        // Check for old course files in project root and migrate them
        File oldCourseFile = new File(username + "_courses.dat");
        if (oldCourseFile.exists() && !courseFile.exists()) {
            try {
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }
                java.nio.file.Files.move(oldCourseFile.toPath(), courseFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Migrated course file to data folder");
            } catch (IOException e) {
                System.err.println("Error migrating course file: " + e.getMessage());
            }
        }
        
        System.out.println("Loading enrolled courses from: " + courseFile.getAbsolutePath());

        if (!courseFile.exists()) {
            System.out.println("No enrolled courses found for " + username);
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(courseFile))) {
            @SuppressWarnings("unchecked")
            List<Course> loadedCourses = (List<Course>) in.readObject();
            enrolledCourses = loadedCourses != null ? loadedCourses : new ArrayList<>();
            
            // Ensure all courses have proper status enum
            for (Course course : enrolledCourses) {
                if (course.getStatus() == null) {
                    course.setStatus(CourseStatus.NOT_ENROLLED);
                }
            }
            
            System.out.println("Courses loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading courses: " + e.getMessage());
            enrolledCourses = new ArrayList<>();
        }
    }
}
