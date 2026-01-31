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
            System.out.println("Povezano z bazo");

            authService = new AuthService(db);
            loginWindow = new LoginWindow();
            loginWindow.getBtnLogin().addActionListener(e -> handleLogin());

            // zdaj pokažemo okno
            loginWindow.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Napaka: " + e.getMessage());
        }
    }

    private void handleLogin() {


        try {
            String username = loginWindow.getTxtUsername().getText();
            String password = new String(loginWindow.getTxtPassword().getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginWindow, "Vnesite oba polja");
                return;
            }

            boolean success = authService.login(username, password);

            if (success) {
                JOptionPane.showMessageDialog(loginWindow, "Prijava uspešna!");
                loginWindow.dispose();
                new MainWindow();
            } else {
                JOptionPane.showMessageDialog(loginWindow, "Napačni podatki");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(loginWindow, "Napaka: " + e.getMessage());
        }
    }


}