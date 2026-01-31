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
            // 1️⃣ povežemo bazo
            db = new Database();
            db.connect();

            // 2️⃣ ustvarimo AuthService
            authService = new AuthService(db);

            // 3️⃣ odpremo login UI
            loginWindow = new LoginWindow();

            // 4️⃣ povežemo gumb LOGIN
            loginWindow.getBtnLogin().addActionListener(e -> handleLogin());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogin() {
        try {
            String username = loginWindow.getTxtUsername().getText();
            String password = new String(loginWindow.getTxtPassword().getPassword());

            boolean success = authService.login(username, password);

            if (success) {
                JOptionPane.showMessageDialog(loginWindow, "Prijava uspešna");
                loginWindow.dispose();
                new MainWindow();
            } else {
                JOptionPane.showMessageDialog(loginWindow, "Napačno uporabniško ime ali geslo");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(loginWindow, "Napaka pri prijavi");
        }
    }
}
