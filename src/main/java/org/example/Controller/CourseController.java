package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.example.*;
import org.example.repository.ICourseRepository;
import org.example.service.IEnrollmentService;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CourseController {

    @FXML private VBox vboxCourses;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;

    private Learner learner;
    private IEnrollmentService enrollmentService;
    private ICourseRepository repo;

    private static final String[] LEVELS = {"Beginner", "Intermediate", "Advanced"};
    private static final String[] RANDOM_TOPICS = {
            "python", "mathematics", "computer_science", "history", "biology",
            "music", "art", "literature", "engineering", "physics"
    };
    private Random rand = new Random();

    public void setLearner(Learner learner) {
        // For backward compatibility, create services if not provided
        this.learner = learner;
        if (this.enrollmentService == null) {
            this.enrollmentService = org.example.factory.ServiceFactory.getInstance().getEnrollmentService();
        }
        if (this.repo == null) {
            this.repo = org.example.factory.ServiceFactory.getInstance().getCourseRepository();
        }
        loadCourses("");
    }

    public void setLearnerAndServices(Learner learner, IEnrollmentService enrollmentService, ICourseRepository repo) {
        this.learner = learner;
        this.enrollmentService = enrollmentService;
        this.repo = repo;
        loadCourses("");
    }

    @FXML
    private void initialize() {
        if (searchBtn != null) {
            searchBtn.setOnAction(e -> {
                String term = searchField.getText().trim();
                if (term.isEmpty()) {
                    term = RANDOM_TOPICS[rand.nextInt(RANDOM_TOPICS.length)];
                } else {
                    // Replace spaces with underscores for Open Library API
                    term = term.replace(" ", "_");
                }
                repo.refreshFromApi(term);
                loadCourses(term);
            });
        }
    }

    private void loadCourses(String searchTerm) {
        if (vboxCourses == null || repo == null || learner == null) return;

        vboxCourses.getChildren().clear();
        List<Course> courses = repo.getAll();

        // Show message if no results found
        if (courses.isEmpty()) {
            Label noResultsLabel = new Label("No results found. Please try a different search term.");
            noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20px;");
            vboxCourses.getChildren().add(noResultsLabel);
            return;
        }

        // Determine the learner's allowed level
        String allowedLevel = learner.getAllowedLevel(10); // You should have logic inside Learner to track completed courses
        long intermediateCompleted = learner.getCompletedCount("Intermediate"); // Track Intermediate courses completed

        for (Course c : courses) {
            // Assign random level if missing
            if (c.getLevel() == null || c.getLevel().isEmpty()) {
                c.setLevel(LEVELS[rand.nextInt(LEVELS.length)]);
            }

            if (c.getStatus() == null) c.setStatus(org.example.model.CourseStatus.NOT_ENROLLED);

            VBox card = new VBox(12);
            card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
            card.setPrefWidth(720);
            card.setMaxWidth(720);

            Label lblTitle = new Label(c.getTitle());
            lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-wrap-text: true;");
            lblTitle.setPrefWidth(680);

            HBox infoBox = new HBox(15);
            infoBox.setAlignment(Pos.CENTER_LEFT);
            
            // Level badge
            String levelColor = c.getLevel().equalsIgnoreCase("Beginner") ? "#10b981" : 
                               c.getLevel().equalsIgnoreCase("Intermediate") ? "#3b82f6" : "#8b5cf6";
            Label lblLevel = new Label(c.getLevel());
            lblLevel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 5 12; -fx-background-color: " + levelColor + "; -fx-background-radius: 15;");

            // Status badge
            String statusStr = c.getStatusString();
            String statusColor = "Completed".equalsIgnoreCase(statusStr) ? "#10b981" : 
                                "Enrolled".equalsIgnoreCase(statusStr) ? "#3b82f6" : "#94a3b8";
            Label lblStatus = new Label(statusStr);
            lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 5 12; -fx-background-color: " + statusColor + "; -fx-background-radius: 15;");
            
            infoBox.getChildren().addAll(lblLevel, lblStatus);

            // Enroll button
            Button btnEnroll = new Button("Enroll Now >");
            boolean canEnroll = learner.canEnroll(c);
            boolean isBeginnerOrIntermediate = c.getLevel().equalsIgnoreCase("Beginner") || c.getLevel().equalsIgnoreCase("Intermediate");
            btnEnroll.setDisable(!(canEnroll || intermediateCompleted >= 10));
            
            if (btnEnroll.isDisable()) {
                btnEnroll.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: default;");
            } else {
                btnEnroll.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 6, 0, 0, 2);");
            }

            btnEnroll.setOnAction(e -> {
                if (learner.canEnroll(c)) {
                    learner.enroll(c);
                    c.setStatus(org.example.model.CourseStatus.IN_PROGRESS);
                    lblStatus.setText("Enrolled");
                    btnEnroll.setDisable(true);

                    // Navigate to course content after enrollment
                    navigateToCourseContent(c);
                }
            });

            HBox actionBox = new HBox(btnEnroll);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(lblTitle, infoBox, actionBox);

            // Modern hover effect
            card.setOnMouseEntered(e -> {
                if (!btnEnroll.isDisable()) {
                    card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 3);");
                }
            });
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
            });

            vboxCourses.getChildren().add(card);
        }
    }

    private void navigateToCourseContent(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CourseContent.fxml"));
            Parent root = loader.load();
            
            CourseContentController contentCtrl = loader.getController();
            // Pass EnrollmentService if available, otherwise let CourseContentController create its own
            if (enrollmentService != null) {
                contentCtrl.setLearnerAndCourse(learner, course, enrollmentService);
            } else {
                contentCtrl.setLearnerAndCourse(learner, course);
            }
            
            // Find the Dashboard's contentPane and load CourseContent into it
            Parent sceneRoot = vboxCourses.getScene().getRoot();
            StackPane contentPane = findContentPane(sceneRoot);
            if (contentPane != null) {
                contentPane.getChildren().setAll(root);
            } else {
                // Fallback: try to get from parent
                javafx.scene.Node node = vboxCourses;
                while (node != null && !(node instanceof StackPane)) {
                    node = node.getParent();
                }
                if (node instanceof StackPane) {
                    ((StackPane) node).getChildren().setAll(root);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // If navigation fails, just refresh the courses list
            loadCourses(searchField != null ? searchField.getText().trim() : "");
        }
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

}
