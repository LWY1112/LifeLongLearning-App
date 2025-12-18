package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.*;
import org.example.service.IEnrollmentService;

import java.io.IOException;
import java.util.List;

public class CourseContentController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label levelLabel;
    @FXML private Label publishDateLabel;
    @FXML private Label topicsLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private VBox contentVBox;
    @FXML private Button markCompletedBtn;
    @FXML private Button backBtn;
    
    private StackPane dashboardContentPane;

    private Learner learner;
    private Course course;
    private ApiClient apiClient;
    private IEnrollmentService enrollmentService;

    public void setLearnerAndCourse(Learner learner, Course course) {
        this.learner = learner;
        this.course = course;
        this.apiClient = new ApiClient();
        // Create EnrollmentService if not provided (for backward compatibility)
        if (this.enrollmentService == null) {
            this.enrollmentService = org.example.factory.ServiceFactory.getInstance().getEnrollmentService();
        }
        
        // Find the Dashboard's contentPane from the scene
        if (backBtn != null && backBtn.getScene() != null) {
            Parent root = backBtn.getScene().getRoot();
            dashboardContentPane = findContentPane(root);
        }
        
        loadCourseContent();
    }

    public void setLearnerAndCourse(Learner learner, Course course, IEnrollmentService enrollmentService) {
        this.learner = learner;
        this.course = course;
        this.apiClient = new ApiClient();
        this.enrollmentService = enrollmentService;
        
        // Find the Dashboard's contentPane from the scene
        if (backBtn != null && backBtn.getScene() != null) {
            Parent root = backBtn.getScene().getRoot();
            dashboardContentPane = findContentPane(root);
        }
        
        loadCourseContent();
    }
    
    private StackPane findContentPane(Parent root) {
        // Check if this is a StackPane (contentPane from Dashboard)
        if (root instanceof StackPane) {
            return (StackPane) root;
        }
        
        // Search in children recursively
        if (root.getChildrenUnmodifiable() != null) {
            for (javafx.scene.Node node : root.getChildrenUnmodifiable()) {
                if (node instanceof StackPane) {
                    return (StackPane) node;
                }
                if (node instanceof Parent) {
                    StackPane found = findContentPane((Parent) node);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }

    private void loadCourseContent() {
        if (course == null) return;

        // Set basic course information
        titleLabel.setText(course.getTitle());
        levelLabel.setText("Level: " + course.getLevel());
        
        // Fetch detailed content from API
        CourseContent content = apiClient.fetchCourseContent(course.getWorkKey());
        
        if (content != null) {
            // Set author
            if (content.getAuthors() != null && !content.getAuthors().isEmpty()) {
                authorLabel.setText("Author(s): " + content.getAuthors());
            } else {
                authorLabel.setText("Author: Unknown");
            }
            
            // Set publish date
            if (content.getPublishDate() != null && !content.getPublishDate().isEmpty()) {
                publishDateLabel.setText("Published: " + content.getPublishDate());
            } else {
                publishDateLabel.setText("Published: Unknown");
            }
            
            // Set topics
            if (content.getTopics() != null && !content.getTopics().isEmpty()) {
                topicsLabel.setText("Topics: " + content.getTopics());
            } else {
                topicsLabel.setText("Topics: " + course.getCategory());
            }
            
            // Set description
            if (content.getDescription() != null && !content.getDescription().isEmpty()) {
                descriptionTextArea.setText(content.getDescription());
            } else {
                descriptionTextArea.setText("No description available for this course. " +
                        "This course teaches: " + course.getTeachesSkill());
            }
            
            // Generate course content sections (chapters/modules)
            generateCourseContentSections(content);
        } else {
            // Fallback if API fails
            authorLabel.setText("Author: Unknown");
            publishDateLabel.setText("Published: Unknown");
            topicsLabel.setText("Topics: " + course.getCategory());
            descriptionTextArea.setText("This course teaches: " + course.getTeachesSkill());
            generateCourseContentSections(null);
        }
        
        // Check if already completed
        if (org.example.model.CourseStatus.COMPLETED.equals(course.getStatus())) {
            markCompletedBtn.setDisable(true);
            markCompletedBtn.setText("Already Completed");
        }
    }

    private void generateCourseContentSections(CourseContent content) {
        contentVBox.getChildren().clear();
        
        // Use chapters and content from API if available
        List<String> chapters = content != null ? content.getChapters() : null;
        List<String> chapterContents = content != null ? content.getChapterContent() : null;
        
        if (chapters != null && !chapters.isEmpty() && chapterContents != null && chapterContents.size() == chapters.size()) {
            // Use API-generated chapters
            for (int i = 0; i < chapters.size(); i++) {
                VBox moduleBox = new VBox(12);
                moduleBox.setPadding(new Insets(20));
                moduleBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12;");
                moduleBox.setPrefWidth(720);
                moduleBox.setMaxWidth(720);
                
                Label moduleTitle = new Label("Chapter " + (i + 1) + ": " + chapters.get(i));
                moduleTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
                moduleTitle.setWrapText(true);
                moduleTitle.setPrefWidth(680);
                
                TextArea moduleContent = new TextArea(chapterContents.get(i));
                moduleContent.setEditable(false);
                moduleContent.setWrapText(true);
                moduleContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 12;");
                moduleContent.setPrefWidth(680);
                moduleContent.setPrefRowCount(Math.min(chapterContents.get(i).split("\n").length + 2, 15));
                
                moduleBox.getChildren().addAll(moduleTitle, moduleContent);
                contentVBox.getChildren().add(moduleBox);
            }
        } else {
            // Fallback: Generate generic modules if API didn't provide chapters
            String[] modules = {
                "Introduction to " + course.getTitle(),
                "Fundamentals and Core Concepts",
                "Advanced Topics and Applications",
                "Practical Exercises and Examples",
                "Summary and Next Steps"
            };
            
            for (int i = 0; i < modules.length; i++) {
                VBox moduleBox = new VBox(12);
                moduleBox.setPadding(new Insets(20));
                moduleBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12;");
                moduleBox.setPrefWidth(720);
                moduleBox.setMaxWidth(720);
                
                Label moduleTitle = new Label("Module " + (i + 1) + ": " + modules[i]);
                moduleTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
                moduleTitle.setWrapText(true);
                moduleTitle.setPrefWidth(680);
                
                TextArea moduleContent = new TextArea(generateModuleContent(modules[i], i));
                moduleContent.setEditable(false);
                moduleContent.setWrapText(true);
                moduleContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 12;");
                moduleContent.setPrefWidth(680);
                moduleContent.setPrefRowCount(8);
                
                moduleBox.getChildren().addAll(moduleTitle, moduleContent);
                contentVBox.getChildren().add(moduleBox);
            }
        }
    }

    private String generateModuleContent(String moduleTitle, int moduleIndex) {
        String baseContent = "In this module, you will learn about " + moduleTitle.toLowerCase() + ".\n\n";
        
        switch (moduleIndex) {
            case 0:
                return baseContent + "This introductory module covers the basics and sets the foundation for your learning journey. " +
                       "You'll understand key concepts and terminology that will be used throughout the course.\n\n" +
                       "Key topics include:\n" +
                       "• Overview of the subject matter\n" +
                       "• Important terminology and definitions\n" +
                       "• Learning objectives and outcomes\n" +
                       "• Course structure and expectations";
            case 1:
                return baseContent + "Dive deep into the fundamental principles and core concepts. " +
                       "This module provides essential knowledge that forms the backbone of the subject matter.\n\n" +
                       "You will explore:\n" +
                       "• Core principles and theories\n" +
                       "• Fundamental concepts and their applications\n" +
                       "• Building blocks for advanced learning\n" +
                       "• Practical examples and demonstrations";
            case 2:
                return baseContent + "Explore advanced topics and real-world applications. " +
                       "Learn how to apply your knowledge in practical scenarios and solve complex problems.\n\n" +
                       "Topics covered:\n" +
                       "• Advanced techniques and methods\n" +
                       "• Real-world case studies\n" +
                       "• Problem-solving strategies\n" +
                       "• Best practices and industry standards";
            case 3:
                return baseContent + "Practice what you've learned through hands-on exercises and examples. " +
                       "This module includes practical activities to reinforce your understanding.\n\n" +
                       "Activities include:\n" +
                       "• Step-by-step tutorials\n" +
                       "• Practice exercises\n" +
                       "• Hands-on projects\n" +
                       "• Self-assessment opportunities";
            case 4:
                return baseContent + "Review key concepts and plan your next steps. " +
                       "This final module helps consolidate your learning and guides you on continuing your education.\n\n" +
                       "You will:\n" +
                       "• Review all major concepts\n" +
                       "• Assess your understanding\n" +
                       "• Plan your continued learning journey\n" +
                       "• Access additional resources";
            default:
                return baseContent + "Continue learning and practicing to master this topic.";
        }
    }

    @FXML
    private void handleMarkCompleted() {
        if (learner == null || course == null) return;

        // Store level before completion to check for upgrade
        String oldLevel = learner.getLevel();

        // Mark course as completed (this will also check for level upgrade)
        enrollmentService.completeCourse(learner, course);
            course.setStatus(org.example.model.CourseStatus.COMPLETED);
        
        // Check if level was upgraded
        String newLevel = learner.getLevel();
        String levelProgress = learner.getLevelWithProgress();
        boolean levelUpgraded = !oldLevel.equals(newLevel);
        
        // Disable button
        markCompletedBtn.setDisable(true);
        markCompletedBtn.setText("Completed");
        
        // Show success message with level information
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Completed");
        alert.setHeaderText(null);
        
        StringBuilder message = new StringBuilder();
        message.append("Congratulations! You have completed: ").append(course.getTitle()).append("\n\n");
        message.append("Your current level: ").append(levelProgress);
        
        // Show level up message if upgraded
        if (levelUpgraded) {
            if ("Intermediate".equalsIgnoreCase(newLevel)) {
                message.append("\n\n🎉 LEVEL UP! You've been upgraded to Intermediate level!");
                message.append("\nYou can now enroll in Intermediate courses!");
            } else if ("Advanced".equalsIgnoreCase(newLevel)) {
                message.append("\n\n🎉 LEVEL UP! You've been upgraded to Advanced level!");
                message.append("\nYou can now enroll in Advanced courses!");
            }
        }
        
        alert.setContentText(message.toString());
        alert.showAndWait();
        
        // Navigate back to courses after user closes the alert
        handleBack();
    }

    @FXML
    private void handleBack() {
        try {
            // Load Course.fxml into Dashboard's contentPane
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Course.fxml"));
            Parent root = loader.load();
            
            CourseController courseCtrl = loader.getController();
            // Pass EnrollmentService if available to maintain consistency
            if (enrollmentService != null) {
                // Need to get CourseRepository - use ServiceFactory
                org.example.repository.ICourseRepository repo = org.example.factory.ServiceFactory.getInstance().getCourseRepository();
                courseCtrl.setLearnerAndServices(learner, enrollmentService, repo);
            } else {
                courseCtrl.setLearner(learner);
            }
            
            // Find the Dashboard's contentPane and load Course into it
            if (dashboardContentPane != null) {
                dashboardContentPane.getChildren().setAll(root);
            } else {
                // Fallback: try to find it from the scene
                Parent sceneRoot = backBtn.getScene().getRoot();
                StackPane contentPane = findContentPane(sceneRoot);
                if (contentPane != null) {
                    contentPane.getChildren().setAll(root);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Could not find Dashboard content area.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load Courses page.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

