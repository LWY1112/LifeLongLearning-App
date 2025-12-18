package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TargetSkillStrategy implements RecommendationStrategy {

    private static final int MIN_RECOMMEND = 15;

    @Override
    public List<Course> recommend(Learner learner, List<Course> courses) {
        List<Course> recommended = new ArrayList<>();
        String allowedLevel = learner.getAllowedLevel(10);

        // Filter courses based on learner's level and enrollment status
        for (Course c : courses) {
            // Assign level if missing
            if (c.getLevel() == null || c.getLevel().isEmpty()) {
                c.setLevel(allowedLevel);
            }

            // Include courses that:
            // 1. Match the learner's allowed level (or below)
            // 2. Are NOT enrolled
            // 3. Are NOT completed
            // 4. Learner can enroll in (meets prerequisites)
            boolean levelMatches = c.getLevel().equalsIgnoreCase(allowedLevel) ||
                    (allowedLevel.equals("Intermediate") && c.getLevel().equalsIgnoreCase("Beginner")) ||
                    (allowedLevel.equals("Advanced") && (c.getLevel().equalsIgnoreCase("Beginner") || c.getLevel().equalsIgnoreCase("Intermediate")));

            if (levelMatches && 
                !learner.isEnrolled(c) && 
                !learner.isCompleted(c) &&
                learner.canEnroll(c)) {
                recommended.add(c);
            }
        }

        // Shuffle randomly for variety
        Collections.shuffle(recommended);

        // Ensure at least MIN_RECOMMEND courses by duplicating if needed
        List<Course> finalList = new ArrayList<>();
        while (finalList.size() < MIN_RECOMMEND && !recommended.isEmpty()) {
            for (Course c : recommended) {
                if (finalList.size() >= MIN_RECOMMEND) break;
                finalList.add(c);
            }
        }

        return finalList;
    }
}
