package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.Course;
import org.example.Learner;
import org.example.service.IEnrollmentService;

import java.io.IOException;
import java.util.List;

public class EnrollController {

    private Learner learner;
    private IEnrollmentService enrollmentService;

    @FXML private VBox vboxEnrolled;
    @FXML private Button btnProfile, btnCourses, btnRecommendations, btnEnrolled, btnLogout;

    public void setLearner(Learner learner) {
        // For backward compatibility, create service if not provided
        this.learner = learner;
        if (this.enrollmentService == null) {
            this.enrollmentService = org.example.factory.ServiceFactory.getInstance().getEnrollmentService();
        }
        loadEnrolledCourses();
    }

    public void setLearnerAndService(Learner learner, IEnrollmentService enrollmentService) {
        this.learner = learner;
        this.enrollmentService = enrollmentService;
        loadEnrolledCourses();
    }

    @FXML
    private void initialize() {
        if (btnProfile != null) btnProfile.setOnAction(e -> System.out.println("Go to Profile"));
        if (btnCourses != null) btnCourses.setOnAction(e -> System.out.println("Go to Courses"));
        if (btnRecommendations != null) btnRecommendations.setOnAction(e -> System.out.println("Go to Recommendations"));
        if (btnEnrolled != null) btnEnrolled.setOnAction(e -> loadEnrolledCourses());
        if (btnLogout != null) btnLogout.setOnAction(e -> System.out.println("Logout"));
    }

    private void loadEnrolledCourses() {
        if (vboxEnrolled == null || learner == null) return;

        vboxEnrolled.getChildren().clear();

        List<Course> enrolled = learner.getEnrolledCourses();
        if (enrolled.isEmpty()) {
            vboxEnrolled.getChildren().add(new Label("No enrolled courses."));
            return;
        }

        for (Course c : enrolled) {
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

            HBox actionBox = new HBox(10);
            actionBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnView = new Button("View Course >");
            btnView.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 6, 0, 0, 2);");
            btnView.setOnAction(e -> navigateToCourseContent(c));

            Button btnComplete = new Button("Complete");
            btnComplete.setDisable("Completed".equalsIgnoreCase(c.getStatusString()));
            if (btnComplete.isDisable()) {
                btnComplete.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: default;");
            } else {
                btnComplete.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.3), 6, 0, 0, 2);");
            }

            btnComplete.setOnAction(e -> {
                // Store level before completion to check for upgrade
                String oldLevel = learner.getLevel();
                
                // Mark course as completed (this will also check for level upgrade)
                enrollmentService.completeCourse(learner, c);
                c.setStatus(org.example.model.CourseStatus.COMPLETED);
                lblStatus.setText("Status: Completed");
                btnComplete.setDisable(true);
                
                // Check if level was upgraded
                String newLevel = learner.getLevel();
                String levelProgress = learner.getLevelWithProgress();
                boolean levelUpgraded = !oldLevel.equals(newLevel);
                
                // Show success message with level information
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Course Completed");
                alert.setHeaderText(null);
                
                StringBuilder message = new StringBuilder();
                message.append("Congratulations! You have completed: ").append(c.getTitle()).append("\n\n");
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
                
                // Refresh the enrolled courses list to show updated status
                loadEnrolledCourses();
            });

            actionBox.getChildren().addAll(btnView, btnComplete);

            card.getChildren().addAll(lblTitle, infoBox, actionBox);
            
            // Modern hover effect
            card.setOnMouseEntered(e -> {
                card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 3);");
            });
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
            });
            
            vboxEnrolled.getChildren().add(card);
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
            Parent sceneRoot = vboxEnrolled.getScene().getRoot();
            StackPane contentPane = findContentPane(sceneRoot);
            if (contentPane != null) {
                contentPane.getChildren().setAll(root);
            } else {
                // Fallback: try to get from parent
                javafx.scene.Node node = vboxEnrolled;
                while (node != null && !(node instanceof StackPane)) {
                    node = node.getParent();
                }
                if (node instanceof StackPane) {
                    ((StackPane) node).getChildren().setAll(root);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Could not find Dashboard content area.");
                    alert.showAndWait();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed to load course content.");
            alert.showAndWait();
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
