package test;

import org.example.Course;
import org.example.Learner;
import org.example.TargetSkillStrategy;
import test.MockUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for TargetSkillStrategy.
 * Tests recommendation filtering logic.
 */
class TargetSkillStrategyTest {
    private TargetSkillStrategy strategy;
    private MockUserRepository mockRepository;
    private Learner learner;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        java.io.File testDataFile = new java.io.File("data", "testuser_courses.dat");
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
        
        strategy = new TargetSkillStrategy();
        mockRepository = new MockUserRepository();
        
        java.util.Map<String, String> userData = new java.util.HashMap<>();
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
    void testRecommend_FiltersByLevel() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Beginner Course", "Category", "Skill", "Beginner", "Provider"));
        courses.add(new Course("Intermediate Course", "Category", "Skill", "Intermediate", "Provider"));
        courses.add(new Course("Advanced Course", "Category", "Skill", "Advanced", "Provider"));
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Strategy ensures minimum 15 recommendations, so it will duplicate the Beginner course
        assertTrue(recommendations.size() >= 15);
        // All recommendations should be Beginner level
        assertTrue(recommendations.stream().allMatch(c -> "Beginner".equals(c.getLevel())));
    }

    @Test
    void testRecommend_ExcludesEnrolledCourses() {
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course("Course 1", "Category", "Skill", "Beginner", "Provider");
        Course course2 = new Course("Course 2", "Category", "Skill", "Beginner", "Provider");
        courses.add(course1);
        courses.add(course2);
        
        learner.enroll(course1);
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should exclude enrolled course, but strategy ensures minimum 15
        assertTrue(recommendations.size() >= 15);
        // All recommendations should be Course 2 (duplicated to reach minimum)
        assertTrue(recommendations.stream().allMatch(c -> "Course 2".equals(c.getTitle())));
    }

    @Test
    void testRecommend_ExcludesCompletedCourses() {
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course("Course 1", "Category", "Skill", "Beginner", "Provider");
        Course course2 = new Course("Course 2", "Category", "Skill", "Beginner", "Provider");
        courses.add(course1);
        courses.add(course2);
        
        learner.enroll(course1);
        learner.completeCourse(course1);
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should exclude completed course, but strategy ensures minimum 15
        assertTrue(recommendations.size() >= 15);
        // All recommendations should be Course 2 (duplicated to reach minimum)
        assertTrue(recommendations.stream().allMatch(c -> "Course 2".equals(c.getTitle())));
    }

    @Test
    void testRecommend_AssignsLevelToCoursesWithoutLevel() {
        List<Course> courses = new ArrayList<>();
        Course course = new Course("Course 1", "Category", "Skill", null, "Provider");
        course.setLevel(null);
        courses.add(course);
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should assign Beginner level, and ensure minimum 15 recommendations
        assertTrue(recommendations.size() >= 15);
        assertEquals("Beginner", recommendations.get(0).getLevel());
    }

    @Test
    void testRecommend_IntermediateLearnerCanAccessBeginnerAndIntermediate() {
        // Upgrade learner to Intermediate
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            learner.completeCourse(course);
        }
        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Beginner Course", "Category", "Skill", "Beginner", "Provider"));
        courses.add(new Course("Intermediate Course", "Category", "Skill", "Intermediate", "Provider"));
        courses.add(new Course("Advanced Course", "Category", "Skill", "Advanced", "Provider"));
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should recommend Beginner and Intermediate (duplicated to reach minimum 15), but not Advanced
        assertTrue(recommendations.size() >= 15);
        assertTrue(recommendations.stream().anyMatch(c -> c.getTitle().equals("Beginner Course")));
        assertTrue(recommendations.stream().anyMatch(c -> c.getTitle().equals("Intermediate Course")));
        assertFalse(recommendations.stream().anyMatch(c -> c.getTitle().equals("Advanced Course")));
    }

    @Test
    void testRecommend_AdvancedLearnerCanAccessAllLevels() {
        // Upgrade learner to Advanced
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
        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Beginner Course", "Category", "Skill", "Beginner", "Provider"));
        courses.add(new Course("Intermediate Course", "Category", "Skill", "Intermediate", "Provider"));
        courses.add(new Course("Advanced Course", "Category", "Skill", "Advanced", "Provider"));
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should recommend all levels (duplicated to reach minimum 15)
        assertTrue(recommendations.size() >= 15);
        assertTrue(recommendations.stream().anyMatch(c -> c.getTitle().equals("Beginner Course")));
        assertTrue(recommendations.stream().anyMatch(c -> c.getTitle().equals("Intermediate Course")));
        assertTrue(recommendations.stream().anyMatch(c -> c.getTitle().equals("Advanced Course")));
    }

    @Test
    void testRecommend_EnsuresMinimumRecommendations() {
        List<Course> courses = new ArrayList<>();
        // Add only 5 courses
        for (int i = 0; i < 5; i++) {
            courses.add(new Course("Course " + i, "Category", "Skill", "Beginner", "Provider"));
        }
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        // Should ensure at least 15 recommendations (by duplicating)
        assertTrue(recommendations.size() >= 15);
    }

    @Test
    void testRecommend_EmptyList() {
        List<Course> courses = new ArrayList<>();
        
        List<Course> recommendations = strategy.recommend(learner, courses);
        
        assertTrue(recommendations.isEmpty());
    }
}


