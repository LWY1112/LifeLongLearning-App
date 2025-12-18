package test;

import org.example.model.LearnerLevel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LearnerLevel enum.
 * Tests level progression and access control.
 */
class LearnerLevelTest {

    @Test
    void testGetDisplayName() {
        assertEquals("Beginner", LearnerLevel.BEGINNER.getDisplayName());
        assertEquals("Intermediate", LearnerLevel.INTERMEDIATE.getDisplayName());
        assertEquals("Advanced", LearnerLevel.ADVANCED.getDisplayName());
    }

    @Test
    void testGetCoursesRequiredPerLevel() {
        assertEquals(10, LearnerLevel.getCoursesRequiredPerLevel());
    }

    @Test
    void testGetNextLevel() {
        assertEquals(LearnerLevel.INTERMEDIATE, LearnerLevel.BEGINNER.getNextLevel());
        assertEquals(LearnerLevel.ADVANCED, LearnerLevel.INTERMEDIATE.getNextLevel());
        assertNull(LearnerLevel.ADVANCED.getNextLevel());
    }

    @Test
    void testCanAccess_Beginner() {
        assertTrue(LearnerLevel.BEGINNER.canAccess(LearnerLevel.BEGINNER));
        assertFalse(LearnerLevel.BEGINNER.canAccess(LearnerLevel.INTERMEDIATE));
        assertFalse(LearnerLevel.BEGINNER.canAccess(LearnerLevel.ADVANCED));
    }

    @Test
    void testCanAccess_Intermediate() {
        assertTrue(LearnerLevel.INTERMEDIATE.canAccess(LearnerLevel.BEGINNER));
        assertTrue(LearnerLevel.INTERMEDIATE.canAccess(LearnerLevel.INTERMEDIATE));
        assertFalse(LearnerLevel.INTERMEDIATE.canAccess(LearnerLevel.ADVANCED));
    }

    @Test
    void testCanAccess_Advanced() {
        assertTrue(LearnerLevel.ADVANCED.canAccess(LearnerLevel.BEGINNER));
        assertTrue(LearnerLevel.ADVANCED.canAccess(LearnerLevel.INTERMEDIATE));
        assertTrue(LearnerLevel.ADVANCED.canAccess(LearnerLevel.ADVANCED));
    }

    @Test
    void testCanAccess_Null() {
        assertFalse(LearnerLevel.BEGINNER.canAccess(null));
        assertFalse(LearnerLevel.INTERMEDIATE.canAccess(null));
        assertFalse(LearnerLevel.ADVANCED.canAccess(null));
    }

    @Test
    void testFromString_ValidNames() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString("Beginner"));
        assertEquals(LearnerLevel.INTERMEDIATE, LearnerLevel.fromString("Intermediate"));
        assertEquals(LearnerLevel.ADVANCED, LearnerLevel.fromString("Advanced"));
    }

    @Test
    void testFromString_EnumNames() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString("BEGINNER"));
        assertEquals(LearnerLevel.INTERMEDIATE, LearnerLevel.fromString("INTERMEDIATE"));
        assertEquals(LearnerLevel.ADVANCED, LearnerLevel.fromString("ADVANCED"));
    }

    @Test
    void testFromString_CaseInsensitive() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString("beginner"));
        assertEquals(LearnerLevel.INTERMEDIATE, LearnerLevel.fromString("INTERMEDIATE"));
        assertEquals(LearnerLevel.ADVANCED, LearnerLevel.fromString("AdVaNcEd"));
    }

    @Test
    void testFromString_Null() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString(null));
    }

    @Test
    void testFromString_Empty() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString(""));
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString("   "));
    }

    @Test
    void testFromString_Invalid() {
        assertEquals(LearnerLevel.BEGINNER, LearnerLevel.fromString("InvalidLevel"));
    }
}


