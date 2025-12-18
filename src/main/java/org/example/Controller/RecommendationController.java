package org.example.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.Course;
import org.example.Learner;
import org.example.RecommendationEngine;

import java.util.List;
import java.util.Random;

public class RecommendationController {

    private Learner learner;
    private RecommendationEngine engine;

    @FXML private VBox vboxCourses;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button btnProfile, btnCourses, btnRecommendations, btnEnrolled, btnLogout;

    private static final String[] RANDOM_TOPICS = {
            "python", "mathematics", "computer_science", "history", "biology",
            "music", "art", "literature", "engineering", "physics"
    };
    private Random rand = new Random();

    public void setLearnerAndEngine(Learner learner, RecommendationEngine engine) {
        this.learner = learner;
        this.engine = engine;
        loadRecommendedCourses(""); // load random topic
    }

    @FXML
    private void initialize() {
        if (searchBtn != null && searchField != null) {
            searchBtn.setOnAction(e -> {
                String query = searchField.getText();
                loadRecommendedCourses(query != null && !query.isBlank() ? query : "");
            });
        }

        if (btnProfile != null) btnProfile.setOnAction(e -> System.out.println("Go to Profile"));
        if (btnCourses != null) btnCourses.setOnAction(e -> System.out.println("Go to Courses"));
        if (btnRecommendations != null)
            btnRecommendations.setOnAction(e -> System.out.println("Already in Recommendations"));
        if (btnEnrolled != null) btnEnrolled.setOnAction(e -> System.out.println("Go to Enrolled Courses"));
        if (btnLogout != null) btnLogout.setOnAction(e -> System.out.println("Logout"));
    }

    private void loadRecommendedCourses(String searchTerm) {
        if (vboxCourses == null || engine == null || learner == null) return;

        vboxCourses.getChildren().clear();

        // pick random topic if search empty
        if (searchTerm == null || searchTerm.isBlank()) {
            searchTerm = RANDOM_TOPICS[rand.nextInt(RANDOM_TOPICS.length)];
        }

        // Refresh courses from API
        engine.getRepo().refreshFromApi(searchTerm);
        List<Course> allCourses = engine.getRepo().getAll();

        // Use the recommendation engine's strategy to get recommendations
        List<Course> recommendedCourses = engine.recommend(learner, allCourses);

        if (recommendedCourses.isEmpty()) {
            vboxCourses.getChildren().add(new Label("No recommended courses available."));
            return;
        }

        // Display recommended courses
        for (Course c : recommendedCourses) {
            displayCourseCard(c);
        }
    }


    private void displayCourseCard(Course c) {
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
        String status = c.getStatusString() != null ? c.getStatusString() : "Not Enrolled";
        String statusColor = "Completed".equalsIgnoreCase(status) ? "#10b981" : 
                            "Enrolled".equalsIgnoreCase(status) ? "#3b82f6" : "#94a3b8";
        Label lblStatus = new Label(status);
        lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 5 12; -fx-background-color: " + statusColor + "; -fx-background-radius: 15;");
        
        infoBox.getChildren().addAll(lblLevel, lblStatus);

        Button btnEnroll = new Button("Enroll Now >");
        btnEnroll.setDisable(!learner.canEnroll(c));
        
        if (btnEnroll.isDisable()) {
            btnEnroll.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: default;");
        } else {
            btnEnroll.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 6, 0, 0, 2);");
        }

        btnEnroll.setOnAction(e -> {
            if (learner.canEnroll(c)) {
                learner.enroll(c);
                lblStatus.setText("Enrolled");
                btnEnroll.setDisable(true);
                loadRecommendedCourses(""); // refresh with new random topic
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
