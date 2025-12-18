package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.example.*;
import org.example.factory.ServiceFactory;
import org.example.repository.ICourseRepository;
import org.example.service.IEnrollmentService;

import java.io.IOException;

/**
 * Main controller for the dashboard.
 * Week 3 - Factory Pattern: Uses ServiceFactory for dependency creation.
 * Week 3 - Decoupling: Uses interfaces for loose coupling.
 */
public class MainController {

    private Learner learner; // Set after login
    private final RecommendationEngine recEngine;
    private final IEnrollmentService enrollmentService;
    private final ICourseRepository repo;

    @FXML private StackPane contentPane; // To dynamically load content

    @FXML private Button btnProfile;
    @FXML private Button btnCourses;
    @FXML private Button btnRecommendations;
    @FXML private Button btnEnrolled;
    @FXML private Button btnLogout;

    @FXML private TextField searchField; // Optional for Courses
    @FXML private Button searchBtn;      // Optional for Courses

    public MainController() {
        // Week 3 - Factory Pattern: Use ServiceFactory to get services
        ServiceFactory factory = ServiceFactory.getInstance();
        this.repo = factory.getCourseRepository();
        this.enrollmentService = factory.getEnrollmentService();
        this.recEngine = new RecommendationEngine(new TargetSkillStrategy(), repo);
    }

    public void setLearner(Learner learner) {
        if (learner == null) throw new RuntimeException("Learner cannot be null");
        this.learner = learner;
    }

    @FXML
    private void initialize() {
        // Set fixed size for contentPane to ensure it does not resize when loading new pages
        contentPane.setPrefSize(1000, 750); // Fix the size of the StackPane

        // Sidebar buttons action handling
        btnProfile.setOnAction(e -> loadPage("/Profile.fxml"));
        btnCourses.setOnAction(e -> loadPage("/Course.fxml"));
        btnRecommendations.setOnAction(e -> loadPage("/Recommendation.fxml"));
        btnEnrolled.setOnAction(e -> loadPage("/Enroll.fxml"));
        btnLogout.setOnAction(e -> handleLogout());
        
        // Add hover effects to sidebar buttons
        addHoverEffect(btnProfile);
        addHoverEffect(btnCourses);
        addHoverEffect(btnRecommendations);
        addHoverEffect(btnEnrolled);
    }
    
    private void addHoverEffect(Button button) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + " -fx-background-color: rgba(59,130,246,0.2);");
        });
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });

        // Search button for Courses
        if (searchBtn != null && searchField != null) {
            searchBtn.setOnAction(e -> {
                String query = searchField.getText();
                if (query != null && !query.isEmpty()) {
                    repo.refreshFromApi(query);
                    loadPage("/Course.fxml");
                }
            });
        }
    }

    private void loadPage(String fxmlPath) {
        if (learner == null) return;

        try {
            // Load the content page (like Profile, Course, etc.)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pass the learner object and services to each page controller
            Object controller = loader.getController();
            if (controller instanceof ProfileController profileCtrl) {
                profileCtrl.setLearner(learner);
            } else if (controller instanceof CourseController courseCtrl) {
                courseCtrl.setLearnerAndServices(learner, enrollmentService, repo);
            } else if (controller instanceof RecommendationController recCtrl) {
                recCtrl.setLearnerAndEngine(learner, recEngine);
            } else if (controller instanceof EnrollController enrollCtrl) {
                enrollCtrl.setLearnerAndService(learner, enrollmentService);
            }

            // Set the loaded content to the StackPane (it will respect the fixed size set earlier)
            contentPane.getChildren().setAll(root); // Load new page

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Implement scene switching back to Login screen
        System.out.println("Logging out...");
        try {
            // Resource file is `login.fxml` (lowercase). Using the correct case is critical once packaged.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene newScene = new Scene(root, 1000, 750); // Set the fixed size for the entire window
            stage.setScene(newScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
