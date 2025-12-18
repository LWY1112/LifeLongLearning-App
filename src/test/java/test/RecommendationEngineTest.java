package test;

import org.example.Course;
import org.example.Learner;
import org.example.RecommendationEngine;
import org.example.RecommendationStrategy;
import org.example.repository.ICourseRepository;
import test.MockUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for RecommendationEngine.
 * Tests the Strategy pattern implementation.
 */
class RecommendationEngineTest {
    private RecommendationEngine engine;
    private MockRecommendationStrategy mockStrategy;
    private MockCourseRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockStrategy = new MockRecommendationStrategy();
        mockRepository = new MockCourseRepository();
        engine = new RecommendationEngine(mockStrategy, mockRepository);
    }

    @Test
    void testRecommendationEngineCreation() {
        assertNotNull(engine);
        assertSame(mockStrategy, engine.getStrategy());
        assertSame(mockRepository, engine.getRepo());
    }

    @Test
    void testRecommendationEngineCreation_NullStrategy() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RecommendationEngine(null, mockRepository);
        });
    }

    @Test
    void testRecommendationEngineCreation_NullRepository() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RecommendationEngine(mockStrategy, null);
        });
    }

    @Test
    void testRecommend() {
        Learner learner = createTestLearner();
        List<Course> courses = createTestCourses();
        
        List<Course> recommendations = engine.recommend(learner, courses);
        
        assertNotNull(recommendations);
        assertTrue(mockStrategy.wasCalled);
        assertEquals(learner, mockStrategy.lastLearner);
        assertEquals(courses, mockStrategy.lastCourses);
    }

    @Test
    void testRecommend_NullLearner() {
        List<Course> courses = createTestCourses();
        
        List<Course> recommendations = engine.recommend(null, courses);
        
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testRecommend_NullCourses() {
        Learner learner = createTestLearner();
        
        List<Course> recommendations = engine.recommend(learner, null);
        
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testRecommend_NullStrategy() {
        RecommendationEngine engineWithNullStrategy = new RecommendationEngine(
            new RecommendationStrategy() {
                @Override
                public List<Course> recommend(Learner learner, List<Course> courses) {
                    return null; // Simulate null strategy
                }
            },
            mockRepository
        );
        
        Learner learner = createTestLearner();
        List<Course> courses = createTestCourses();
        
        List<Course> recommendations = engineWithNullStrategy.recommend(learner, courses);
        
        assertTrue(recommendations.isEmpty());
    }

    private Learner createTestLearner() {
        MockUserRepository mockUserRepo = new MockUserRepository();
        return new Learner("testuser", mockUserRepo);
    }

    private List<Course> createTestCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Java Basics", "Programming", "Java", "Beginner", "Provider"));
        courses.add(new Course("Python Basics", "Programming", "Python", "Beginner", "Provider"));
        return courses;
    }

    // Mock implementation of RecommendationStrategy for testing
    private static class MockRecommendationStrategy implements RecommendationStrategy {
        boolean wasCalled = false;
        Learner lastLearner;
        List<Course> lastCourses;

        @Override
        public List<Course> recommend(Learner learner, List<Course> courses) {
            wasCalled = true;
            lastLearner = learner;
            lastCourses = courses;
            return new ArrayList<>(courses); // Return a copy
        }
    }

    // Mock implementation of ICourseRepository for testing
    private static class MockCourseRepository implements ICourseRepository {
        private List<Course> courses = new ArrayList<>();

        @Override
        public List<Course> getAll() {
            return new ArrayList<>(courses);
        }

        @Override
        public void refreshFromApi(String searchTerm) {
            // Mock implementation
        }

        @Override
        public Course findByTitle(String title) {
            return courses.stream()
                    .filter(c -> c.getTitle().equals(title))
                    .findFirst()
                    .orElse(null);
        }
    }
}


