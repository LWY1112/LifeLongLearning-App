package test;

import org.example.model.UserProfile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserProfile value object.
 * Tests encapsulation and validation.
 */
class UserProfileTest {

    @Test
    void testUserProfileCreation() {
        UserProfile profile = new UserProfile("John Doe", "1234567890", 
                                             "john@example.com", "25");
        
        assertEquals("John Doe", profile.getFullName());
        assertEquals("1234567890", profile.getPhone());
        assertEquals("john@example.com", profile.getEmail());
        assertEquals("25", profile.getAge());
    }

    @Test
    void testUserProfileCreation_WithNullValues() {
        // Constructor should handle null values gracefully
        UserProfile profile = new UserProfile(null, null, null, null);
        
        assertEquals("", profile.getFullName());
        assertEquals("", profile.getPhone());
        assertEquals("", profile.getEmail());
        assertEquals("", profile.getAge());
    }

    @Test
    void testUserProfileCreation_WithEmptyValues() {
        UserProfile profile = new UserProfile("", "  ", null, "");
        
        assertEquals("", profile.getFullName());
        assertEquals("", profile.getPhone());
        assertEquals("", profile.getEmail());
        assertEquals("", profile.getAge());
    }

    @Test
    void testUserProfileCreation_TrimsWhitespace() {
        UserProfile profile = new UserProfile("  John Doe  ", "  1234567890  ", 
                                             "  john@example.com  ", "  25  ");
        
        assertEquals("John Doe", profile.getFullName());
        assertEquals("1234567890", profile.getPhone());
        assertEquals("john@example.com", profile.getEmail());
        assertEquals("25", profile.getAge());
    }

    @Test
    void testSetFullName() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        profile.setFullName("Jane Doe");
        assertEquals("Jane Doe", profile.getFullName());
    }

    @Test
    void testSetFullName_Null() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setFullName(null);
        });
    }

    @Test
    void testSetFullName_Empty() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setFullName("   ");
        });
    }

    @Test
    void testSetPhone() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        profile.setPhone("9876543210");
        assertEquals("9876543210", profile.getPhone());
    }

    @Test
    void testSetPhone_Null() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setPhone(null);
        });
    }

    @Test
    void testSetEmail() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        profile.setEmail("jane@example.com");
        assertEquals("jane@example.com", profile.getEmail());
    }

    @Test
    void testSetEmail_Null() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setEmail(null);
        });
    }

    @Test
    void testSetEmail_InvalidFormat() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setEmail("invalid-email");
        });
    }

    @Test
    void testSetAge() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        profile.setAge("30");
        assertEquals("30", profile.getAge());
    }

    @Test
    void testSetAge_Null() {
        UserProfile profile = new UserProfile("John", "123", "john@test.com", "25");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.setAge(null);
        });
    }
}


