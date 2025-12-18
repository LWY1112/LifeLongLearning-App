package org.example;

import org.example.model.CourseStatus;
import java.io.Serializable;

/**
 * Core entity representing a course.
 * Week 1 - Encapsulation: Proper encapsulation with private fields and controlled access.
 * Week 4 - Composition: Uses CourseStatus enum for status management.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String category;
    private String teachesSkill;
    private String level;
    private String provider;
    private CourseStatus status; // Using enum for type safety
    private String workKey; // Open Library work key for fetching detailed content

    // Constructor to initialize all fields
    public Course(String title, String category, String teachesSkill, String level, String provider) {
        this.title = validateTitle(title);
        this.category = category;
        this.teachesSkill = teachesSkill;
        this.level = level;
        this.provider = provider;
        this.status = CourseStatus.NOT_ENROLLED;
        this.workKey = null;
    }
    
    // Constructor with work key
    public Course(String title, String category, String teachesSkill, String level, String provider, String workKey) {
        this(title, category, teachesSkill, level, provider);
        this.workKey = workKey;
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be null or empty");
        }
        return title.trim();
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = validateTitle(title);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTeachesSkill() {
        return teachesSkill;
    }

    public void setTeachesSkill(String teachesSkill) {
        this.teachesSkill = teachesSkill;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    // Status getter/setter using enum
    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status != null ? status : CourseStatus.NOT_ENROLLED;
    }

    // Backward compatibility: String-based status (for serialization compatibility)
    public String getStatusString() {
        return status.getDisplayName();
    }

    public void setStatus(String status) {
        this.status = CourseStatus.fromString(status);
    }

    public String getWorkKey() {
        return workKey;
    }

    public void setWorkKey(String workKey) {
        this.workKey = workKey;
    }

    @Override
    public String toString() {
        return "Course{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", teachesSkill='" + teachesSkill + '\'' +
                ", level='" + level + '\'' +
                ", provider='" + provider + '\'' +
                ", status='" + status.getDisplayName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return title != null && title.equals(course.title);
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}
