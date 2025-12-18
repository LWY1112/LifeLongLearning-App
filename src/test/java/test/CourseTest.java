package test;

import org.example.Course;
import org.example.model.CourseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Course entity.
 * Tests encapsulation, validation, and status management.
 */
class CourseTest {
    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course("Java Programming", "Programming", "Java", "Beginner", "Tech Academy");
    }

    @Test
    void testCourseCreation() {
        assertNotNull(course);
        assertEquals("Java Programming", course.getTitle());
        assertEquals("Programming", course.getCategory());
        assertEquals("Java", course.getTeachesSkill());
        assertEquals("Beginner", course.getLevel());
        assertEquals("Tech Academy", course.getProvider());
        assertEquals(CourseStatus.NOT_ENROLLED, course.getStatus());
    }

    @Test
    void testCourseCreationWithWorkKey() {
        Course courseWithKey = new Course("Python Basics", "Programming", "Python", 
                                         "Beginner", "Tech Academy", "OL123456W");
        assertEquals("OL123456W", courseWithKey.getWorkKey());
    }

    @Test
    void testTitleValidation_NullTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Course(null, "Category", "Skill", "Beginner", "Provider");
        });
    }

    @Test
    void testTitleValidation_EmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Course("   ", "Category", "Skill", "Beginner", "Provider");
        });
    }

    @Test
    void testTitleTrimming() {
        Course courseWithSpaces = new Course("  Java Programming  ", "Category", 
                                            "Skill", "Beginner", "Provider");
        assertEquals("Java Programming", courseWithSpaces.getTitle());
    }

    @Test
    void testSetTitle() {
        course.setTitle("Advanced Java");
        assertEquals("Advanced Java", course.getTitle());
    }

    @Test
    void testSetTitle_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            course.setTitle(null);
        });
    }

    @Test
    void testSetStatus() {
        course.setStatus(CourseStatus.ENROLLED);
        assertEquals(CourseStatus.ENROLLED, course.getStatus());
        
        course.setStatus(CourseStatus.COMPLETED);
        assertEquals(CourseStatus.COMPLETED, course.getStatus());
    }

    @Test
    void testSetters() {
        course.setCategory("Web Development");
        course.setTeachesSkill("JavaScript");
        course.setLevel("Intermediate");
        course.setProvider("New Provider");
        course.setWorkKey("OL789W");

        assertEquals("Web Development", course.getCategory());
        assertEquals("JavaScript", course.getTeachesSkill());
        assertEquals("Intermediate", course.getLevel());
        assertEquals("New Provider", course.getProvider());
        assertEquals("OL789W", course.getWorkKey());
    }

    @Test
    void testGetStatusString() {
        course.setStatus(CourseStatus.ENROLLED);
        assertEquals("Enrolled", course.getStatusString());
        
        course.setStatus(CourseStatus.COMPLETED);
        assertEquals("Completed", course.getStatusString());
    }
}


