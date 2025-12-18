package org.example.factory;

import org.example.ApiClient;
import org.example.repository.CourseRepository;
import org.example.repository.ICourseRepository;
import org.example.repository.IUserRepository;
import org.example.repository.UserRepository;
import org.example.service.EnrollmentService;
import org.example.service.IEnrollmentService;
import org.example.service.LevelProgressionService;

/**
 * Factory for creating service and repository instances.
 * Week 3 - Factory Pattern: Centralized creation of services and repositories.
 * Week 3 - Decoupling: Reduces coupling by centralizing object creation.
 */
public class ServiceFactory {
    private static ServiceFactory instance;
    private final IUserRepository userRepository;
    private final ICourseRepository courseRepository;
    private final IEnrollmentService enrollmentService;
    private final LevelProgressionService levelProgressionService;

    /**
     * Private constructor for singleton pattern
     */
    private ServiceFactory() {
        this.userRepository = UserRepository.getInstance();
        this.courseRepository = new CourseRepository(new ApiClient());
        this.levelProgressionService = new LevelProgressionService();
        this.enrollmentService = new EnrollmentService(levelProgressionService);
    }

    /**
     * Get singleton instance
     * Week 3 - Singleton Pattern: Ensures single factory instance
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    /**
     * Get user repository instance
     * @return IUserRepository instance
     */
    public IUserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Get course repository instance
     * @return ICourseRepository instance
     */
    public ICourseRepository getCourseRepository() {
        return courseRepository;
    }

    /**
     * Get enrollment service instance
     * @return IEnrollmentService instance
     */
    public IEnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    /**
     * Get level progression service instance
     * @return LevelProgressionService instance
     */
    public LevelProgressionService getLevelProgressionService() {
        return levelProgressionService;
    }

    /**
     * Create a new course repository with custom API client
     * Week 3 - Factory Pattern: Allows creation of specialized instances
     * @param apiClient Custom API client
     * @return ICourseRepository instance
     */
    public ICourseRepository createCourseRepository(ApiClient apiClient) {
        return new CourseRepository(apiClient);
    }
}

