package org.example.service;

import org.example.Course;
import org.example.Learner;
import org.example.model.CourseStatus;
import org.example.model.LearnerLevel;

/**
 * Service for managing course enrollment operations.
 * Week 3 - Decoupling: Implements IEnrollmentService interface.
 * Week 4 - Composition: Uses LevelProgressionService through composition.
 */
public class EnrollmentService implements IEnrollmentService {
    private final LevelProgressionService levelProgressionService;

    public EnrollmentService() {
        this.levelProgressionService = new LevelProgressionService();
    }

    public EnrollmentService(LevelProgressionService levelProgressionService) {
        this.levelProgressionService = levelProgressionService != null 
            ? levelProgressionService 
            : new LevelProgressionService();
    }

    @Override
    public boolean enroll(Learner learner, Course course) {
        if (learner == null || course == null) {
            throw new IllegalArgumentException("Learner and Course cannot be null");
        }
        
        if (isAlreadyEnrolled(learner, course)) {
            return false;
        }

        // Only assign level if missing (from API)
        if (course.getLevel() == null || course.getLevel().isEmpty()) {
            LearnerLevel allowedLevel = levelProgressionService.getAllowedLevel(learner);
            course.setLevel(allowedLevel.getDisplayName());
        }

        // Set status using enum
        course.setStatus(CourseStatus.ENROLLED);

        // Add course to learner
        learner.enroll(course);
        return true;
    }

    @Override
    public boolean isAlreadyEnrolled(Learner learner, Course course) {
        if (learner == null || course == null) {
            return false;
        }
        return learner.getEnrolledCourses().stream()
                .anyMatch(c -> c.getTitle().equals(course.getTitle()));
    }

    @Override
    public void completeCourse(Learner learner, Course course) {
        if (learner == null || course == null) {
            throw new IllegalArgumentException("Learner and Course cannot be null");
        }
        
        for (Course c : learner.getEnrolledCourses()) {
            if (c.getTitle().equals(course.getTitle())) {
                c.setStatus(CourseStatus.COMPLETED);
                break;
            }
        }
        
        // Use level progression service for level upgrade
        levelProgressionService.checkAndUpgradeLevel(learner);
        
        // Learner's completeCourse method will handle persistence
        learner.completeCourse(course);
    }
}

