package test;

import org.example.Course;
import org.example.Learner;
import org.example.model.CourseStatus;
import test.MockUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for Learner entity.
 * Tests encapsulation, course enrollment, and level progression.
 */
class LearnerTest {
    private MockUserRepository mockRepository;
    private Learner learner;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        java.io.File testDataFile = new java.io.File("data", "testuser_courses.dat");
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
        
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
    void testLearnerCreation() {
        assertNotNull(learner);
        assertEquals("testuser", learner.getUsername());
        assertEquals("password123", learner.getPassword());
        assertEquals("Beginner", learner.getLevel());
        assertEquals("Test User", learner.getFullName());
        assertEquals("1234567890", learner.getPhone());
        assertEquals("test@example.com", learner.getEmail());
        assertEquals("25", learner.getAge());
    }

    @Test
    void testLearnerCreation_NullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Learner(null, mockRepository);
        });
    }

    @Test
    void testLearnerCreation_EmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Learner("   ", mockRepository);
        });
    }

    @Test
    void testLearnerCreation_UserNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Learner("nonexistent", mockRepository);
        });
    }

    @Test
    void testSetUsername() {
        learner.setUsername("newuser");
        assertEquals("newuser", learner.getUsername());
    }

    @Test
    void testSetUsername_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            learner.setUsername(null);
        });
    }

    @Test
    void testSetLevel() {
        learner.setLevel("Intermediate");
        assertEquals("Intermediate", learner.getLevel());
        
        learner.setLevel("Advanced");
        assertEquals("Advanced", learner.getLevel());
    }

    @Test
    void testEnrollCourse() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        assertFalse(learner.isEnrolled(course));
        learner.enroll(course);
        
        assertTrue(learner.isEnrolled(course));
        assertEquals(1, learner.getEnrolledCourses().size());
        assertEquals(CourseStatus.ENROLLED, course.getStatus());
    }

    @Test
    void testEnrollCourse_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            learner.enroll(null);
        });
    }

    @Test
    void testEnrollCourse_Duplicate() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        learner.enroll(course);
        learner.enroll(course); // Try to enroll again
        
        assertEquals(1, learner.getEnrolledCourses().size());
    }

    @Test
    void testCompleteCourse() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        learner.enroll(course);
        
        assertFalse(learner.isCompleted(course));
        learner.completeCourse(course);
        
        assertTrue(learner.isCompleted(course));
        assertEquals(CourseStatus.COMPLETED, course.getStatus());
    }

    @Test
    void testCompleteCourse_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            learner.completeCourse(null);
        });
    }

    @Test
    void testIsEnrolled() {
        Course course1 = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        Course course2 = new Course("Python Basics", "Programming", "Python", "Beginner", "Provider");
        
        assertFalse(learner.isEnrolled(course1));
        learner.enroll(course1);
        assertTrue(learner.isEnrolled(course1));
        assertFalse(learner.isEnrolled(course2));
    }

    @Test
    void testIsEnrolled_Null() {
        assertFalse(learner.isEnrolled(null));
    }

    @Test
    void testIsCompleted() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        
        assertFalse(learner.isCompleted(course));
        learner.enroll(course);
        assertFalse(learner.isCompleted(course));
        learner.completeCourse(course);
        assertTrue(learner.isCompleted(course));
    }

    @Test
    void testIsCompleted_Null() {
        assertFalse(learner.isCompleted(null));
    }

    @Test
    void testGetCompletedCount() {
        Course course1 = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        Course course2 = new Course("Python Basics", "Programming", "Python", "Beginner", "Provider");
        
        learner.enroll(course1);
        learner.enroll(course2);
        
        assertEquals(0, learner.getCompletedCount("Beginner"));
        
        learner.completeCourse(course1);
        assertEquals(1, learner.getCompletedCount("Beginner"));
        
        learner.completeCourse(course2);
        assertEquals(2, learner.getCompletedCount("Beginner"));
    }

    @Test
    void testAddCurrentSkill() {
        assertEquals(0, learner.getCurrentSkills().size());
        
        learner.addCurrentSkill("Java");
        assertEquals(1, learner.getCurrentSkills().size());
        assertTrue(learner.getCurrentSkills().contains("Java"));
    }

    @Test
    void testAddCurrentSkill_Duplicate() {
        learner.addCurrentSkill("Java");
        learner.addCurrentSkill("Java");
        assertEquals(1, learner.getCurrentSkills().size());
    }

    @Test
    void testAddCurrentSkill_Null() {
        learner.addCurrentSkill(null);
        assertEquals(0, learner.getCurrentSkills().size());
    }

    @Test
    void testAddTargetSkill() {
        assertEquals(0, learner.getTargetSkills().size());
        
        learner.addTargetSkill("Python");
        assertEquals(1, learner.getTargetSkills().size());
        assertTrue(learner.getTargetSkills().contains("Python"));
    }

    @Test
    void testGetEnrolledCourses_DefensiveCopy() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        learner.enroll(course);
        
        var courses1 = learner.getEnrolledCourses();
        var courses2 = learner.getEnrolledCourses();
        
        assertNotSame(courses1, courses2);
        assertEquals(courses1.size(), courses2.size());
    }

    @Test
    void testCanEnroll() {
        Course beginnerCourse = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        Course intermediateCourse = new Course("Advanced Java", "Programming", "Java", "Intermediate", "Provider");
        
        // Beginner can enroll in beginner courses
        assertTrue(learner.canEnroll(beginnerCourse));
        
        // Beginner cannot enroll in intermediate courses
        assertFalse(learner.canEnroll(intermediateCourse));
    }

    @Test
    void testCanEnroll_AlreadyEnrolled() {
        Course course = new Course("Java Basics", "Programming", "Java", "Beginner", "Provider");
        learner.enroll(course);
        
        assertFalse(learner.canEnroll(course));
    }

    @Test
    void testCanEnroll_Null() {
        assertFalse(learner.canEnroll(null));
    }
}


