package controller;

import db.Database;
import service.AuthService;
import ui.LoginWindow;
import ui.MainWindow;

import javax.swing.*;

public class AppController {
    private Database db;
    private AuthService authService;
    private LoginWindow loginWindow;

    public void startApp() {
        try {
            db = new Database();
            db.connect();

            authService = new AuthService(db);

            showLoginWindow();

        } catch (Exception e) {
            System.out.println(null,"Napaka: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLoginWindow() {
        loginWindow = new LoginWindow();
        loginWindow.setVisible(true);

        loginWindow.getBtnLogin().addActionListener(e -> checkLogin());
    }

    private void checkLogin() {
        String username = loginWindow.getTxtUsername().getText().trim();
        String password = new String(loginWindow.getTxtPassword().getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginWindow,"Izpolni vsa polja!");
            return;
        }

        try {
            boolean loginSuccessful = authService.login(username, password);

            if (loginSuccessful) {
                loginWindow.dispose();

                new MainWindow();
            } else {
                JOptionPane.showMessageDialog("Napačno uporabniško ime ali geslo");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog("Napaka: " + e.getMessage());
        }
    }
}