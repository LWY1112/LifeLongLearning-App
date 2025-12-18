package org.example;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents detailed content information for a course
 */
public class CourseContent {
    private String title;
    private String authors;
    private String description;
    private String topics;
    private String publishDate;
    private List<String> chapters; // Course chapters/modules
    private List<String> chapterContent; // Detailed content for each chapter

    public CourseContent(String title, String authors, String description, String topics, String publishDate) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.topics = topics;
        this.publishDate = publishDate;
        this.chapters = new ArrayList<>();
        this.chapterContent = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getTopics() {
        return topics;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public List<String> getChapters() {
        return chapters;
    }

    public void setChapters(List<String> chapters) {
        this.chapters = chapters;
    }

    public List<String> getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(List<String> chapterContent) {
        this.chapterContent = chapterContent;
    }
}

