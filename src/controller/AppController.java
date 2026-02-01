package controller;

import db.Database;
import service.AuthService;
import ui.LoginWindow;
import ui.MainWindow;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
            JOptionPane.showMessageDialog(null, "Napaka pri zagonu: " + e.getMessage());
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
            JOptionPane.showMessageDialog(loginWindow, "Izpolni vsa polja!");
            return;
        }

        try {
            boolean loginSuccessful = authService.login(username, password);

            if (loginSuccessful) {
                loginWindow.dispose();

                new MainWindow(this);
            } else {
                JOptionPane.showMessageDialog(loginWindow, "Napačno uporabniško ime ali geslo");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(loginWindow, "Napaka: " + e.getMessage());
        }
    }

    public void loadEmployees(DefaultTableModel model) throws Exception {
        Connection conn = db.getConnection();

        String sql = "SELECT * FROM get_all_employees()";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        model.setRowCount(0);

        while (rs.next()) {
            Object[] row = {
                    rs.getString("ime"),
                    rs.getString("priimek"),
                    rs.getString("delovno_mesto"),
                    rs.getString("oddelek"),
                    rs.getFloat("placa"),
                    rs.getDate("datum_zaposlitve")
            };
            model.addRow(row);
        }

        rs.close();
        ps.close();
    }

}