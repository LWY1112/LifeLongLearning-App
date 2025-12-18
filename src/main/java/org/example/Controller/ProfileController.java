package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Course;
import org.example.Learner;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProfileController {

    // Profile fields
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAge;
    @FXML
    private Label lblLevel;
    @FXML
    private Button btnSaveProfile;

    // Password fields
    @FXML
    private PasswordField txtCurrentPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmNewPassword;

    // Course history table
    @FXML
    private TableView<Course> tableCourseHistory;
    @FXML
    private TableColumn<Course, String> colCourseName;
    @FXML
    private TableColumn<Course, String> colLevel;
    @FXML
    private TableColumn<Course, String> colStatus;

    private Learner learner;
    private String originalUsername;

    public void setLearner(Learner learner) {
        this.learner = learner;
        if (learner == null) throw new RuntimeException("Learner cannot be null");
        this.originalUsername = learner.getUsername();

        loadProfile();
        loadCourseHistory();

        // Bind save button
        if (btnSaveProfile != null) {
            btnSaveProfile.setOnAction(e -> saveProfile());
        }
    }

    private void loadProfile() {
        txtUsername.setText(learner.getUsername());
        txtFullName.setText(learner.getFullName());
        txtPhone.setText(learner.getPhone());
        txtEmail.setText(learner.getEmail());
        txtAge.setText(learner.getAge());
        
        // Display level with progress
        if (lblLevel != null) {
            lblLevel.setText(learner.getLevelWithProgress());
        }
    }

    private void loadCourseHistory() {
        // Setup table columns
        colCourseName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        colLevel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLevel()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatusString()));

        ObservableList<Course> enrolledCourses = FXCollections.observableArrayList(learner.getEnrolledCourses());
        tableCourseHistory.setItems(enrolledCourses);
    }

    private void saveProfile() {
        // Update learner's profile data
        learner.setUsername(txtUsername.getText().trim());
        learner.setFullName(txtFullName.getText().trim());
        learner.setPhone(txtPhone.getText().trim());
        learner.setEmail(txtEmail.getText().trim());
        learner.setAge(txtAge.getText().trim());

        // Save updated user info to user.txt
        if (learner.saveToDatabase(originalUsername)) {
            System.out.println("Profile updated successfully!");
            showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to update profile.");
        }

        // Only handle password change if new password fields are filled
        if (!txtNewPassword.getText().trim().isEmpty() || !txtConfirmNewPassword.getText().trim().isEmpty()) {
            handlePasswordChange();
        }
    }

    private void handlePasswordChange() {
        // Get password fields' input
        String currentPassword = txtCurrentPassword.getText().trim();
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmNewPassword.getText().trim();

        // If new password fields are not empty, proceed with validation
        if (!newPassword.isEmpty() && !confirmPassword.isEmpty()) {
            // Validate password change
            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "New passwords do not match.");
                return;
            }

            // Check if the current password is correct
            if (learner.getPassword() == null || !learner.getPassword().equals(currentPassword)) {
                showAlert(Alert.AlertType.ERROR, "Current password is incorrect.");
                return;
            }

            // Update the password
            learner.setPassword(newPassword);

            // Save the updated password
            if (learner.saveToDatabase(originalUsername)) {
                showAlert(Alert.AlertType.INFORMATION, "Password updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update password.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);  // Optionally set a header text
        alert.setContentText(message);  // Set the message content
        alert.showAndWait();  // Display the alert and wait for the user to close it
    }

}
