package org.example.repository;

import org.example.Course;
import java.util.List;

/**
 * Repository interface for course data access.
 * Week 3 - Decoupling: Interface for loose coupling and OCP.
 * Week 4 - Flexibility: Allows different implementations (API, Database, etc.)
 */
public interface ICourseRepository {
    /**
     * Get all courses currently in the repository
     * @return List of all courses
     */
    List<Course> getAll();

    /**
     * Refresh courses from external source (e.g., API)
     * @param searchTerm Search term to fetch courses
     */
    void refreshFromApi(String searchTerm);

    /**
     * Find a course by title
     * @param title Course title
     * @return Course if found, null otherwise
     */
    Course findByTitle(String title);
}

