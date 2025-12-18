package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import org.example.UserDatabase;
import org.example.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

public class ResetPasswordController {

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ageField;
    @FXML
    private Label messageLabel;
    @FXML
    private VBox usernamePane;
    @FXML
    private VBox verificationPane;
    @FXML
    private VBox newPasswordPane;
    @FXML
    private VBox confirmPasswordPane;
    @FXML
    private Button verifyUsernameBtn;
    @FXML
    private Button verifyInfoBtn;
    @FXML
    private Button submitBtn;

    private String currentUsername;

    public void initialize() {
        // Initialize visibility - only username pane should be visible
        usernamePane.setVisible(true);
        usernamePane.setManaged(true);
        
        verificationPane.setVisible(false);
        verificationPane.setManaged(false);
        
        newPasswordPane.setVisible(false);
        newPasswordPane.setManaged(false);
        
        confirmPasswordPane.setVisible(false);
        confirmPasswordPane.setManaged(false);
        
        submitBtn.setVisible(false);
        submitBtn.setManaged(false);
        
        messageLabel.setText("");
    }

    @FXML
    private void handleVerifyUsername() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter your username.");
            return;
        }

        // Check if the username exists in the UserDatabase
        Map<String, String> user = UserDatabase.getInstance().getUser(username);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Username not found.");
            return;
        }

        // Store username for later use
        currentUsername = username;

        // Show verification fields
        usernamePane.setVisible(false);
        usernamePane.setManaged(false);
        
        verificationPane.setVisible(true);
        verificationPane.setManaged(true);
        
        messageLabel.setText("");
        emailField.clear();
        ageField.clear();
    }

    @FXML
    private void handleVerifyInfo() {
        String enteredEmail = emailField.getText().trim();
        String enteredAge = ageField.getText().trim();

        if (enteredEmail.isEmpty() || enteredAge.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter both email and age.");
            return;
        }

        // Get user data
        Map<String, String> user = UserDatabase.getInstance().getUser(currentUsername);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "User not found.");
            return;
        }

        String registeredEmail = user.get("email");
        String registeredAge = user.get("age");

        // Verify email and age match
        if (!enteredEmail.equalsIgnoreCase(registeredEmail) || !enteredAge.equals(registeredAge)) {
            showAlert(Alert.AlertType.ERROR, "Email or age does not match. Please try again.");
            emailField.clear();
            ageField.clear();
            return;
        }

        messageLabel.setText("Information verified! Please enter your new password.");
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px; -fx-font-weight: bold;");
        emailField.setDisable(true);
        ageField.setDisable(true);
        verifyInfoBtn.setDisable(true);
        
        // Show password fields
        verificationPane.setVisible(false);
        verificationPane.setManaged(false);
        
        newPasswordPane.setVisible(true);
        newPasswordPane.setManaged(true);
        
        confirmPasswordPane.setVisible(true);
        confirmPasswordPane.setManaged(true);
        
        submitBtn.setVisible(true);
        submitBtn.setManaged(true);
    }

    @FXML
    private void handleSubmit() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter both new password and confirmation.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
            return;
        }

        // Update password by username
        boolean isUpdated = UserDatabase.getInstance().updatePasswordByUsername(currentUsername, newPassword);

        if (isUpdated) {
            showAlert(Alert.AlertType.INFORMATION, "Password successfully reset!");

            // Navigate back to the login screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) messageLabel.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 750);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error loading login screen.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to reset password.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 750);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading login screen.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
