package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.User;
import service.AuthService;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    private AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> login());
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = authService.authenticate(username, password);

        if (user != null) {
            System.out.println("Login successful: " + user.getUsername());
            // tukaj kasneje lahko odpreš dashboard
        } else {
            System.out.println("Login failed!");
        }
    }
}
