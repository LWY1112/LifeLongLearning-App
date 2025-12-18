package org.example.model;

/**
 * Enum representing learner proficiency levels.
 * Week 1 - Encapsulation: Encapsulates valid learner levels.
 */
public enum LearnerLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    private final String displayName;
    private static final int COURSES_REQUIRED_PER_LEVEL = 10;

    LearnerLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static int getCoursesRequiredPerLevel() {
        return COURSES_REQUIRED_PER_LEVEL;
    }

    /**
     * Get the next level in progression
     * @return Next level, or null if already at maximum
     */
    public LearnerLevel getNextLevel() {
        switch (this) {
            case BEGINNER:
                return INTERMEDIATE;
            case INTERMEDIATE:
                return ADVANCED;
            case ADVANCED:
                return null; // Maximum level reached
            default:
                return null;
        }
    }

    /**
     * Check if a level can be accessed from current level
     * @param targetLevel Level to check access for
     * @return true if accessible, false otherwise
     */
    public boolean canAccess(LearnerLevel targetLevel) {
        if (targetLevel == null) return false;
        
        switch (this) {
            case BEGINNER:
                return targetLevel == BEGINNER;
            case INTERMEDIATE:
                return targetLevel == BEGINNER || targetLevel == INTERMEDIATE;
            case ADVANCED:
                return true; // Advanced can access all levels
            default:
                return false;
        }
    }

    /**
     * Convert string to LearnerLevel enum
     * @param level String representation of level
     * @return LearnerLevel enum, defaults to BEGINNER if invalid
     */
    public static LearnerLevel fromString(String level) {
        if (level == null || level.isEmpty()) {
            return BEGINNER;
        }
        for (LearnerLevel ll : values()) {
            if (ll.displayName.equalsIgnoreCase(level.trim()) || ll.name().equalsIgnoreCase(level.trim())) {
                return ll;
            }
        }
        return BEGINNER;
    }
}

