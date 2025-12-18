package org.example;

import org.example.repository.ICourseRepository;
import java.util.List;

public class RecommendationEngine {

    private RecommendationStrategy strategy;
    private ICourseRepository repo;

    public RecommendationEngine(RecommendationStrategy strategy, ICourseRepository repo) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        if (repo == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.strategy = strategy;
        this.repo = repo;
    }

    public ICourseRepository getRepo() {
        return repo;
    }

    public RecommendationStrategy getStrategy() {
        return strategy;
    }

    /**
     * Get recommendations for a learner using the configured strategy
     * @param learner The learner to get recommendations for
     * @param allCourses All available courses to filter from
     * @return List of recommended courses
     */
    public List<Course> recommend(Learner learner, List<Course> allCourses) {
        if (strategy == null || learner == null || allCourses == null) {
            return List.of(); // Return empty list if invalid input
        }
        List<Course> result = strategy.recommend(learner, allCourses);
        return result != null ? result : List.of(); // Handle null return from strategy
    }
}