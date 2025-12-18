package test;

import org.example.Course;
import org.example.Learner;
import org.example.service.EnrollmentService;
import org.example.service.LevelProgressionService;
import test.MockUserRepository;
import org.example.model.CourseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for EnrollmentService.
 * Tests enrollment and course completion logic.
 */
class EnrollmentServiceTest {
    private EnrollmentService service;
    private MockUserRepository mockRepository;
    private Learner learner;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        java.io.File testDataFile = new java.io.File("data", "testuser_courses.dat");
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
        
        service = new EnrollmentService();
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
    void testEnroll_Success() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        boolean result = service.enroll(learner, course);
        
        assertTrue(result);
        assertTrue(learner.isEnrolled(course));
        assertEquals(CourseStatus.ENROLLED, course.getStatus());
    }

    @Test
    void testEnroll_AlreadyEnrolled() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        service.enroll(learner, course);
        boolean result = service.enroll(learner, course); // Try to enroll again
        
        assertFalse(result);
        assertEquals(1, learner.getEnrolledCourses().size());
    }

    @Test
    void testEnroll_NullLearner() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.enroll(null, course);
        });
    }

    @Test
    void testEnroll_NullCourse() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.enroll(learner, null);
        });
    }

    @Test
    void testEnroll_CourseWithoutLevel() {
        Course course = new Course("Java Basics", "Programming", "Java", null, "Provider");
        course.setLevel(null); // Explicitly set to null
        
        service.enroll(learner, course);
        
        // Should assign Beginner level (learner's allowed level)
        assertEquals("Beginner", course.getLevel());
        assertEquals(CourseStatus.ENROLLED, course.getStatus());
    }

    @Test
    void testEnroll_CourseWithEmptyLevel() {
        Course course = new Course("Java Basics", "Programming", "Java", "", "Provider");
        
        service.enroll(learner, course);
        
        // Should assign Beginner level
        assertEquals("Beginner", course.getLevel());
    }

    @Test
    void testIsAlreadyEnrolled() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        assertFalse(service.isAlreadyEnrolled(learner, course));
        
        learner.enroll(course);
        assertTrue(service.isAlreadyEnrolled(learner, course));
    }

    @Test
    void testIsAlreadyEnrolled_NullLearner() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        assertFalse(service.isAlreadyEnrolled(null, course));
    }

    @Test
    void testIsAlreadyEnrolled_NullCourse() {
        assertFalse(service.isAlreadyEnrolled(learner, null));
    }

    @Test
    void testCompleteCourse() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        learner.enroll(course);
        
        assertFalse(learner.isCompleted(course));
        
        service.completeCourse(learner, course);
        
        assertTrue(learner.isCompleted(course));
        assertEquals(CourseStatus.COMPLETED, course.getStatus());
    }

    @Test
    void testCompleteCourse_NullLearner() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.completeCourse(null, course);
        });
    }

    @Test
    void testCompleteCourse_NullCourse() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.completeCourse(learner, null);
        });
    }

    @Test
    void testCompleteCourse_TriggersLevelUpgrade() {
        // Complete 10 beginner courses
        for (int i = 0; i < 10; i++) {
            Course course = new Course("Course " + i, "Category", "Skill", "Beginner", "Provider");
            learner.enroll(course);
            service.completeCourse(learner, course);
        }
        
        // Should upgrade to Intermediate
        assertEquals("Intermediate", learner.getLevel());
    }

    @Test
    void testEnrollmentService_WithCustomLevelProgressionService() {
        LevelProgressionService customService = new LevelProgressionService();
        EnrollmentService customEnrollmentService = new EnrollmentService(customService);
        
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        boolean result = customEnrollmentService.enroll(learner, course);
        
        assertTrue(result);
        assertTrue(learner.isEnrolled(course));
    }
}


