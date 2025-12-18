package org.example.service;

import org.example.Course;
import org.example.Learner;

/**
 * Service interface for course enrollment operations.
 * Week 3 - Decoupling: Interface for loose coupling.
 * Week 4 - Flexibility: Allows different enrollment implementations.
 */
public interface IEnrollmentService {
    /**
     * Enroll a learner in a course
     * @param learner The learner to enroll
     * @param course The course to enroll in
     * @return true if enrollment successful, false otherwise
     */
    boolean enroll(Learner learner, Course course);

    /**
     * Check if learner is already enrolled in a course
     * @param learner The learner
     * @param course The course
     * @return true if enrolled, false otherwise
     */
    boolean isAlreadyEnrolled(Learner learner, Course course);

    /**
     * Mark a course as completed for a learner
     * @param learner The learner
     * @param course The course to complete
     */
    void completeCourse(Learner learner, Course course);
}

