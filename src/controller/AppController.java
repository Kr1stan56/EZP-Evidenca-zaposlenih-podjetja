package controller;

import db.Database;
import service.AuthService;
import ui.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

public class AppController {

    private Database db;
    private AuthService authService;
    private LoginWindow loginWindow;

    public void startApp() {
        try {
            db = new Database();
            db.connect();

            loadUiConfig();

            authService = new AuthService(db);
            showLoginWindow();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Napaka pri zagonu: " + e.getMessage(),
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    

    private void loadUiConfig() throws Exception {

        UiConfig.FONT_BASE = null;
        UiConfig.FONT_H1 = null;
        UiConfig.FONT_H2 = null;

        Connection conn = db.getConnection();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT name, value FROM public.get_settings(?)"
        );
        ps.setString(1, "ui.");

        ResultSet rs = ps.executeQuery();

        String fontFamily = null;

        while (rs.next()) {
            String k = rs.getString("name");
            String v = rs.getString("value");

            switch (k) {
                case "ui.font.family" -> fontFamily = v;

                case "ui.color.bg.app"  -> UiConfig.BG_APP  = Color.decode(v);
                case "ui.color.bg.bar"  -> UiConfig.BG_BAR  = Color.decode(v);
                case "ui.color.bg.card" -> UiConfig.BG_CARD = Color.decode(v);
                case "ui.color.border"  -> UiConfig.BORDER  = Color.decode(v);

                case "ui.color.text"       -> UiConfig.TEXT       = Color.decode(v);
                case "ui.color.text.muted" -> UiConfig.TEXT_MUTED  = Color.decode(v);

                case "ui.color.primary" -> UiConfig.PRIMARY = Color.decode(v);
                case "ui.color.success" -> UiConfig.SUCCESS = Color.decode(v);
                case "ui.color.danger"  -> UiConfig.DANGER  = Color.decode(v);

                case "ui.pad.outer" -> UiConfig.PAD = Integer.parseInt(v);
                case "ui.pad.inner" -> UiConfig.PAD_INNER = Integer.parseInt(v);

                case "ui.btn.w" -> UiConfig.BTN_W = Integer.parseInt(v);
                case "ui.btn.h" -> UiConfig.BTN_H = Integer.parseInt(v);

                case "ui.table.row.h" -> UiConfig.TABLE_ROW_H = Integer.parseInt(v);
            }
        }

        rs.close();
        ps.close();

        if (fontFamily != null) {
            UiConfig.FONT_BASE = new Font(fontFamily, Font.PLAIN, 12);
            UiConfig.FONT_H1   = new Font(fontFamily, Font.BOLD, 18);
            UiConfig.FONT_H2   = new Font(fontFamily, Font.BOLD, 14);
        }

        if (UiConfig.FONT_BASE == null || UiConfig.PRIMARY == null || UiConfig.BG_APP == null || UiConfig.BORDER == null) {
            throw new IllegalStateException("UI nastavitve niso pravilno naložene (manjkajo ui.* ključi)");
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
            if (authService.login(username, password)) {
                loginWindow.dispose();
                loadUiConfig();
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

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM get_all_employees()");
        ResultSet rs = ps.executeQuery();

        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getFloat(6),
                    rs.getDate(7),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getString(10)
            });
        }

        rs.close();
        ps.close();
    }



    public int addEmployee(
            String ime,
            String priimek,
            String email,
            String telefon,
            float placa,
            Date datumZaposlitve,
            int delovnoMestoId,
            int oddelekId,
            int krajId,
            int izobrazbaId
    ) throws Exception {

        Connection conn = db.getConnection();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT add_employee(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        ps.setString(1, ime);
        ps.setString(2, priimek);
        ps.setString(3, email);
        ps.setString(4, telefon);
        ps.setFloat(5, placa);
        ps.setDate(6, datumZaposlitve);
        ps.setInt(7, delovnoMestoId);
        ps.setInt(8, oddelekId);
        ps.setInt(9, krajId);
        ps.setInt(10, izobrazbaId);

        ResultSet rs = ps.executeQuery();
        rs.next();
        int newId = rs.getInt(1);

        rs.close();
        ps.close();

        return newId;
    }



    public ResultSet getDelovnaMesta() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_delovna_mesta()"
        );
        return ps.executeQuery();
    }

    public ResultSet getOddelki() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_oddelki()"
        );
        return ps.executeQuery();
    }

    public ResultSet getKraji() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_kraji()"
        );
        return ps.executeQuery();
    }



    public ResultSet getEmployeeById(int employeeId) throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_employee_by_id(?)"
        );
        ps.setInt(1, employeeId);
        return ps.executeQuery();
    }

    public void updateEmployee(
            int id,
            String ime,
            String priimek,
            String email,
            String telefon,
            float placa,
            Date datum,
            int delovnoMestoId,
            int oddelekId,
            int krajId
    ) throws Exception {

        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT update_employee(?, ?, ?, ?, ?, ?::numeric, ?::date, ?, ?, ?)"
        );

        ps.setInt(1, id);
        ps.setString(2, ime);
        ps.setString(3, priimek);
        ps.setString(4, email);
        ps.setString(5, telefon);
        ps.setFloat(6, placa);
        ps.setDate(7, datum);
        ps.setInt(8, delovnoMestoId);
        ps.setInt(9, oddelekId);
        ps.setInt(10, krajId);

        ps.execute();
        ps.close();
    }
    public void deleteEmployee(int employeeId) throws Exception {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT delete_employee(?)"
        )) {
            ps.setInt(1, employeeId);
            ps.execute();
        }
    }

}
