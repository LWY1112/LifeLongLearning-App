package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.example.Learner;
import org.example.UserDatabase;

import java.io.IOException;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;

    // Handle Login
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter username and password.");
            return;
        }

        // Get the user details from the database
        Map<String, String> user = UserDatabase.getInstance().getUser(username);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid username.");
            return;
        }

        // Check the password
        String storedPassword = user.get("password");
        if (storedPassword != null && storedPassword.equals(password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login successful! Welcome, " + user.get("fullName"));

            // Load Dashboard.fxml after successful login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent root = loader.load();

                // Get the controller of Dashboard.fxml and set the Learner object
                MainController mainCtrl = loader.getController();
                
                // Create Learner with error handling
                try {
                    Learner learner = new Learner(username);
                    mainCtrl.setLearner(learner);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error loading user data: " + e.getMessage());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Unexpected error: " + e.getMessage());
                    return;
                }

                // Switch scene to Dashboard
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Cannot load Dashboard.fxml: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error loading dashboard: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
        }
    }

    // Show alert method to display errors or info
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Navigate to Register Page
    @FXML
    private void handleGoToRegister() {
        try {
            // Load Register.fxml to allow new users to register
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml"));
            Parent root = loader.load();

            // Get the current stage and switch the scene
            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));  // Set the new scene (Register.fxml)
            stage.show();  // Show the new scene

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load Register page.");
        }
    }
    @FXML
    private void handleForgotPassword() {
        try {
            // Load ResetPassword.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forgotpassword.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and switch to the reset password screen
            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage (Login page)
            stage.setScene(new Scene(root));  // Set the new scene (Reset Password page)
            stage.show();  // Show the new scene

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load Reset Password page.");
        }
    }

}
