package test;

import org.example.Course;
import org.example.Learner;
import org.example.service.LevelProgressionService;
import test.MockUserRepository;
import org.example.model.LearnerLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for LevelProgressionService.
 * Tests level progression logic and validation.
 */
class LevelProgressionServiceTest {
    private LevelProgressionService service;
    private MockUserRepository mockRepository;
    private Learner learner;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        java.io.File testDataFile = new java.io.File("data", "testuser_courses.dat");
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
        
        service = new LevelProgressionService();
        mockRepository = new MockUserRepository();
        
        Map<String, String> userData = new HashMap<>();
        userData.put("username", "testuser");
        userData.put("password", "password123");
        userData.put("fullName", "Test User");
        userData.put("phone", "1234567890");
        userData.put("email", "test@example.com");
        userData.put("age", "25");
        userData.put("level", "Beginner");
        mockRepository.addUser("testuser", userData);
        
        learner = new Learner("testuser", mockRepository);
    }

    @Test
    void testGetAllowedLevel_Beginner() {
        LearnerLevel allowed = service.getAllowedLevel(learner);
        assertEquals(LearnerLevel.BEGINNER, allowed);
    }

    @Test
    void testGetAllowedLevel_AfterCompletingBeginnerCourses() {
        // Complete 10 beginner courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        LearnerLevel allowed = service.getAllowedLevel(learner);
        assertEquals(LearnerLevel.INTERMEDIATE, allowed);
    }

    @Test
    void testGetAllowedLevel_AfterCompletingIntermediateCourses() {
        // Complete 10 beginner courses first
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Beginner Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        // Upgrade to Intermediate
        service.checkAndUpgradeLevel(learner);
        assertEquals("Intermediate", learner.getLevel());
        
        // Complete 10 intermediate courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Intermediate Course " + i, "Category", "Skill", "Intermediate", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        // Upgrade to Advanced
        service.checkAndUpgradeLevel(learner);
        assertEquals("Advanced", learner.getLevel());
        
        LearnerLevel allowed = service.getAllowedLevel(learner);
        assertEquals(LearnerLevel.ADVANCED, allowed);
    }

    @Test
    void testCheckAndUpgradeLevel_NoUpgrade() {
        // Complete only 5 beginner courses (not enough for upgrade)
        for (int i = 0; i < 5; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        service.checkAndUpgradeLevel(learner);
        assertEquals("Beginner", learner.getLevel());
    }

    @Test
    void testCheckAndUpgradeLevel_UpgradeToIntermediate() {
        // Complete 10 beginner courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        service.checkAndUpgradeLevel(learner);
        assertEquals("Intermediate", learner.getLevel());
    }

    @Test
    void testCheckAndUpgradeLevel_UpgradeToAdvanced() {
        // Complete 10 beginner courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Beginner Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        // Complete 10 intermediate courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Intermediate Course " + i, "Category", "Skill", "Intermediate", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        service.checkAndUpgradeLevel(learner);
        assertEquals("Advanced", learner.getLevel());
    }

    @Test
    void testCheckAndUpgradeLevel_MaximumLevel() {
        // First upgrade to Advanced properly
        // Complete 10 beginner courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Beginner Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        service.checkAndUpgradeLevel(learner);
        
        // Complete 10 intermediate courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Intermediate Course " + i, "Category", "Skill", "Intermediate", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        service.checkAndUpgradeLevel(learner);
        assertEquals("Advanced", learner.getLevel());
        
        // Complete 10 advanced courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Advanced Course " + i, "Category", "Skill", "Advanced", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        service.checkAndUpgradeLevel(learner);
        assertEquals("Advanced", learner.getLevel()); // Should remain Advanced (maximum level)
    }

    @Test
    void testGetLevelWithProgress_Beginner() {
        // Ensure learner has no completed courses
        assertEquals(0, learner.getEnrolledCourses().stream()
            .filter(c -> org.example.model.CourseStatus.COMPLETED.equals(c.getStatus()))
            .count());
        
        String progress = service.getLevelWithProgress(learner);
        assertTrue(progress.contains("Beginner"));
        assertTrue(progress.contains("/10") || progress.contains("0/10"));
        assertTrue(progress.contains("Intermediate") || progress.contains("unlock"));
    }

    @Test
    void testGetLevelWithProgress_Intermediate() {
        // Complete 10 beginner courses to upgrade
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        service.checkAndUpgradeLevel(learner);
        
        // Verify level was upgraded
        assertEquals("Intermediate", learner.getLevel());
        
        String progress = service.getLevelWithProgress(learner);
        assertTrue(progress.contains("Intermediate"));
        assertTrue(progress.contains("/10") || progress.contains("0/10"));
        assertTrue(progress.contains("Advanced") || progress.contains("unlock"));
    }

    @Test
    void testGetLevelWithProgress_Advanced() {
        // Complete courses to reach Advanced
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Beginner Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Intermediate Course " + i, "Category", "Skill", "Intermediate", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        service.checkAndUpgradeLevel(learner);
        
        String progress = service.getLevelWithProgress(learner);
        assertTrue(progress.contains("Advanced"));
        assertTrue(progress.contains("Maximum level reached"));
    }

    @Test
    void testValidateLevel_CorrectsInvalidLevel() {
        // Ensure learner has no completed courses first
        assertEquals(0, learner.getEnrolledCourses().stream()
            .filter(c -> org.example.model.CourseStatus.COMPLETED.equals(c.getStatus()))
            .count());
        
        // Set invalid level (Intermediate without completing Beginner courses)
        learner.setLevel("Intermediate");
        
        service.checkAndUpgradeLevel(learner);
        // Should be corrected to Beginner since no courses completed
        assertEquals("Beginner", learner.getLevel());
    }
}


