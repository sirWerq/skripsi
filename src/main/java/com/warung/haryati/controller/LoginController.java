package com.warung.haryati.controller;

import com.warung.haryati.App;
import com.warung.haryati.util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private HBox passwordContainer;

    @FXML
    private FontIcon eyeIcon;

    @FXML
    private Label errorLabel;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        if (passwordContainer != null) {
            passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> updatePasswordContainerFocus(newVal || passwordTextField.isFocused()));
            passwordTextField.focusedProperty().addListener((obs, oldVal, newVal) -> updatePasswordContainerFocus(newVal || passwordField.isFocused()));
        }
    }

    private void updatePasswordContainerFocus(boolean isFocused) {
        if (isFocused) {
            if (!passwordContainer.getStyleClass().contains("password-container-focused")) {
                passwordContainer.getStyleClass().add("password-container-focused");
            }
        } else {
            passwordContainer.getStyleClass().remove("password-container-focused");
        }
    }

    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            eyeIcon.setIconLiteral("fas-eye");
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
            eyeIcon.setIconLiteral("fas-eye-slash");
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan Password tidak boleh kosong");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                App.setRoot("dashboard");
            } else {
                errorLabel.setText("Username atau Password salah");
            }

        } catch (SQLException e) {
            errorLabel.setText("Kesalahan Database: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            errorLabel.setText("Kesalahan Navigasi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
