package org.example;

import java.util.ArrayList;
import java.util.List;

// Strategy Interface
public interface RecommendationStrategy {
    List<Course> recommend(Learner learner, List<Course> courses);
}