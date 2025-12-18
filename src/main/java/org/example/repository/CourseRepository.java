package org.example.repository;

import org.example.ApiClient;
import org.example.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for course data access using API.
 * Week 3 - Decoupling: Implements ICourseRepository interface for loose coupling.
 * Week 4 - Composition: Uses ApiClient through composition.
 */
public class CourseRepository implements ICourseRepository {
    private final ApiClient apiClient;
    private final List<Course> courses;

    public CourseRepository(ApiClient client) {
        if (client == null) {
            throw new IllegalArgumentException("ApiClient cannot be null");
        }
        this.apiClient = client;
        this.courses = new ArrayList<>();
    }

    @Override
    public List<Course> getAll() {
        return new ArrayList<>(courses); // Return defensive copy
    }

    @Override
    public void refreshFromApi(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        List<Course> fetched = apiClient.fetchCourses(searchTerm);
        courses.clear();
        courses.addAll(fetched);
    }

    @Override
    public Course findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }
        return courses.stream()
                .filter(c -> title.equalsIgnoreCase(c.getTitle()))
                .findFirst()
                .orElse(null);
    }
}

