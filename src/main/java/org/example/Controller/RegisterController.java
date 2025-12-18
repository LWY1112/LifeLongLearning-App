package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import org.example.UserDatabase;

import java.io.IOException;
import java.net.URL;

public class RegisterController {

    @FXML
    private TextField nameField, phoneField, emailField, ageField, usernameField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private Button registerBtn;

    @FXML
    private void handleRegister() {
        // Get user input
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String fullName = nameField.getText().trim();  // Use nameField instead of fullNameField
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String age = ageField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || age.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
            return;
        }

        // Add the user to the database
        boolean added = UserDatabase.getInstance().addUser(username, password, fullName, phone, email, age);

        if (added) {
            showAlert(Alert.AlertType.INFORMATION, "User registered successfully!");
            // Automatically switch to login page
            handleBackToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Username already exists.");
        }
    }


    @FXML
    private void handleBackToLogin() {
        try {
            // Resource file is `login.fxml` (lowercase). Using the correct case is critical once packaged.
            URL fxmlLocation = getClass().getResource("/login.fxml");
            if (fxmlLocation == null) {
                showAlert(AlertType.ERROR, "login.fxml not found!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Failed to load login page.");
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
