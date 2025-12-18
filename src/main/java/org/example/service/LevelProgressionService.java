package org.example.service;

import org.example.Learner;
import org.example.model.LearnerLevel;

/**
 * Service for managing learner level progression.
 * Week 4 - Composition: Separated level progression logic from Learner entity.
 * Week 1 - Encapsulation: Encapsulates level progression business rules.
 */
public class LevelProgressionService {
    private static final int COURSES_REQUIRED_PER_LEVEL = LearnerLevel.getCoursesRequiredPerLevel();

    /**
     * Check and upgrade learner level based on completed courses
     * @param learner The learner to check
     */
    public void checkAndUpgradeLevel(Learner learner) {
        LearnerLevel currentLevel = LearnerLevel.fromString(learner.getLevel());
        LearnerLevel validatedLevel = validateLevel(learner, currentLevel);
        
        if (!validatedLevel.equals(currentLevel)) {
            learner.setLevel(validatedLevel.getDisplayName());
            System.out.println("Level corrected to: " + validatedLevel.getDisplayName());
        }

        LearnerLevel nextLevel = validatedLevel.getNextLevel();
        if (nextLevel != null && canUpgrade(learner, validatedLevel)) {
            learner.setLevel(nextLevel.getDisplayName());
            System.out.println("Congratulations! You've been upgraded to " + nextLevel.getDisplayName() + " level!");
        }
    }

    /**
     * Validate learner level based on actual completed courses
     * @param learner The learner
     * @param currentLevel Current level
     * @return Validated level
     */
    private LearnerLevel validateLevel(Learner learner, LearnerLevel currentLevel) {
        int beginnerCompleted = getCompletedCount(learner, LearnerLevel.BEGINNER);
        int intermediateCompleted = getCompletedCount(learner, LearnerLevel.INTERMEDIATE);

        if (currentLevel == LearnerLevel.INTERMEDIATE && beginnerCompleted < COURSES_REQUIRED_PER_LEVEL) {
            return LearnerLevel.BEGINNER;
        }
        
        if (currentLevel == LearnerLevel.ADVANCED) {
            if (intermediateCompleted < COURSES_REQUIRED_PER_LEVEL) {
                return beginnerCompleted >= COURSES_REQUIRED_PER_LEVEL 
                    ? LearnerLevel.INTERMEDIATE 
                    : LearnerLevel.BEGINNER;
            }
        }

        return currentLevel;
    }

    /**
     * Check if learner can upgrade to next level
     * @param learner The learner
     * @param currentLevel Current level
     * @return true if can upgrade, false otherwise
     */
    private boolean canUpgrade(Learner learner, LearnerLevel currentLevel) {
        int completedCount = getCompletedCount(learner, currentLevel);
        return completedCount >= COURSES_REQUIRED_PER_LEVEL;
    }

    /**
     * Get count of completed courses for a specific level
     * @param learner The learner
     * @param level The level to count
     * @return Number of completed courses
     */
    private int getCompletedCount(Learner learner, LearnerLevel level) {
        return (int) learner.getEnrolledCourses().stream()
                .filter(c -> level.getDisplayName().equalsIgnoreCase(c.getLevel()))
                .filter(c -> org.example.model.CourseStatus.COMPLETED.equals(c.getStatus()))
                .count();
    }

    /**
     * Get level with progress information
     * @param learner The learner
     * @return String describing current level and progress
     */
    public String getLevelWithProgress(Learner learner) {
        LearnerLevel currentLevel = LearnerLevel.fromString(learner.getLevel());
        int completedCount = getCompletedCount(learner, currentLevel);
        LearnerLevel nextLevel = currentLevel.getNextLevel();

        if (nextLevel == null) {
            return currentLevel.getDisplayName() + " (Maximum level reached!)";
        }

        return currentLevel.getDisplayName() + " (" + completedCount + "/" + 
               COURSES_REQUIRED_PER_LEVEL + " courses completed to unlock " + 
               nextLevel.getDisplayName() + ")";
    }

    /**
     * Get allowed level for enrollment based on completed courses
     * @param learner The learner
     * @return Allowed level for enrollment
     */
    public LearnerLevel getAllowedLevel(Learner learner) {
        int beginnerCompleted = getCompletedCount(learner, LearnerLevel.BEGINNER);
        int intermediateCompleted = getCompletedCount(learner, LearnerLevel.INTERMEDIATE);

        if (beginnerCompleted < COURSES_REQUIRED_PER_LEVEL) {
            return LearnerLevel.BEGINNER;
        }
        if (intermediateCompleted < COURSES_REQUIRED_PER_LEVEL) {
            return LearnerLevel.INTERMEDIATE;
        }
        return LearnerLevel.ADVANCED;
    }
}

