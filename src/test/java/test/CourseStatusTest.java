package test;

import org.example.model.CourseStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CourseStatus enum.
 */
class CourseStatusTest {

    @Test
    void testGetDisplayName() {
        assertEquals("Not Enrolled", CourseStatus.NOT_ENROLLED.getDisplayName());
        assertEquals("Enrolled", CourseStatus.ENROLLED.getDisplayName());
        assertEquals("In Progress", CourseStatus.IN_PROGRESS.getDisplayName());
        assertEquals("Completed", CourseStatus.COMPLETED.getDisplayName());
    }

    @Test
    void testFromString_ValidNames() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString("Not Enrolled"));
        assertEquals(CourseStatus.ENROLLED, CourseStatus.fromString("Enrolled"));
        assertEquals(CourseStatus.IN_PROGRESS, CourseStatus.fromString("In Progress"));
        assertEquals(CourseStatus.COMPLETED, CourseStatus.fromString("Completed"));
    }

    @Test
    void testFromString_EnumNames() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString("NOT_ENROLLED"));
        assertEquals(CourseStatus.ENROLLED, CourseStatus.fromString("ENROLLED"));
        assertEquals(CourseStatus.IN_PROGRESS, CourseStatus.fromString("IN_PROGRESS"));
        assertEquals(CourseStatus.COMPLETED, CourseStatus.fromString("COMPLETED"));
    }

    @Test
    void testFromString_CaseInsensitive() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString("not enrolled"));
        assertEquals(CourseStatus.ENROLLED, CourseStatus.fromString("ENROLLED"));
        assertEquals(CourseStatus.COMPLETED, CourseStatus.fromString("completed"));
    }

    @Test
    void testFromString_Null() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString(null));
    }

    @Test
    void testFromString_Empty() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString(""));
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString("   "));
    }

    @Test
    void testFromString_Invalid() {
        assertEquals(CourseStatus.NOT_ENROLLED, CourseStatus.fromString("InvalidStatus"));
    }
}


