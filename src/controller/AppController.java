package controller;

import db.Database;
import service.AuthService;
import ui.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

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

        UiConfig.BG_APP = null;
        UiConfig.BG_BAR = null;
        UiConfig.BG_CARD = null;
        UiConfig.BORDER = null;

        UiConfig.TEXT = null;
        UiConfig.TEXT_MUTED = null;

        UiConfig.PRIMARY = null;
        UiConfig.SUCCESS = null;
        UiConfig.DANGER = null;

        UiConfig.PRIMARY_TEXT = null;
        UiConfig.TABLE_HEADER_BG = null;
        UiConfig.TABLE_HEADER_FG = null;
        UiConfig.TABLE_GRID = null;

        UiConfig.PAD = -1;
        UiConfig.PAD_INNER = -1;
        UiConfig.BTN_W = -1;
        UiConfig.BTN_H = -1;
        UiConfig.TABLE_ROW_H = -1;

        UiConfig.BTN_ICON_W = -1;
        UiConfig.BTN_ICON_H = -1;

        Connection conn = db.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT name, value FROM public.get_settings(?)"
        )) {
            ps.setString(1, "ui.");

            try (ResultSet rs = ps.executeQuery()) {

                String fontFamily = null;
                Integer fontBaseSize = null;
                Integer fontH1Size = null;
                Integer fontH2Size = null;

                while (rs.next()) {
                    String k = rs.getString("name");
                    String v = rs.getString("value");

                    switch (k) {
                        case "ui.font.family"     -> fontFamily = v;
                        case "ui.font.size.base"  -> fontBaseSize = Integer.parseInt(v);
                        case "ui.font.size.h1"    -> fontH1Size = Integer.parseInt(v);
                        case "ui.font.size.h2"    -> fontH2Size = Integer.parseInt(v);

                        case "ui.color.bg.app"    -> UiConfig.BG_APP  = Color.decode(v);
                        case "ui.color.bg.bar"    -> UiConfig.BG_BAR  = Color.decode(v);
                        case "ui.color.bg.card"   -> UiConfig.BG_CARD = Color.decode(v);
                        case "ui.color.border"    -> UiConfig.BORDER  = Color.decode(v);

                        case "ui.color.text"       -> UiConfig.TEXT = Color.decode(v);
                        case "ui.color.text.muted" -> UiConfig.TEXT_MUTED = Color.decode(v);

                        case "ui.color.primary"      -> UiConfig.PRIMARY = Color.decode(v);
                        case "ui.color.primary.text" -> UiConfig.PRIMARY_TEXT = Color.decode(v);
                        case "ui.color.success"      -> UiConfig.SUCCESS = Color.decode(v);
                        case "ui.color.danger"       -> UiConfig.DANGER = Color.decode(v);

                        case "ui.color.table.header.bg" -> UiConfig.TABLE_HEADER_BG = Color.decode(v);
                        case "ui.color.table.header.fg" -> UiConfig.TABLE_HEADER_FG = Color.decode(v);
                        case "ui.color.table.grid"      -> UiConfig.TABLE_GRID = Color.decode(v);
                        case "ui.color.table.bg"        -> UiConfig.TABLE_BG = Color.decode(v);

                        case "ui.pad.outer" -> UiConfig.PAD = Integer.parseInt(v);
                        case "ui.pad.inner" -> UiConfig.PAD_INNER = Integer.parseInt(v);

                        case "ui.btn.w" -> UiConfig.BTN_W = Integer.parseInt(v);
                        case "ui.btn.h" -> UiConfig.BTN_H = Integer.parseInt(v);

                        case "ui.btn.icon.w" -> UiConfig.BTN_ICON_W = Integer.parseInt(v);
                        case "ui.btn.icon.h" -> UiConfig.BTN_ICON_H = Integer.parseInt(v);

                        case "ui.table.row.h" -> UiConfig.TABLE_ROW_H = Integer.parseInt(v);

                        default -> { }
                    }
                }

                if (fontFamily != null && fontBaseSize != null && fontH1Size != null && fontH2Size != null) {
                    UiConfig.FONT_BASE = new Font(fontFamily, Font.PLAIN, fontBaseSize);
                    UiConfig.FONT_H1   = new Font(fontFamily, Font.BOLD,  fontH1Size);
                    UiConfig.FONT_H2   = new Font(fontFamily, Font.BOLD,  fontH2Size);
                }

                StringBuilder missing = new StringBuilder();

                if (UiConfig.FONT_BASE == null) missing.append(" ui.font.*");
                if (UiConfig.BG_APP == null) missing.append(" ui.color.bg.app");
                if (UiConfig.BORDER == null) missing.append(" ui.color.border");
                if (UiConfig.PRIMARY == null) missing.append(" ui.color.primary");
                if (UiConfig.TEXT == null) missing.append(" ui.color.text");

                if (UiConfig.PAD < 0) missing.append(" ui.pad.outer");
                if (UiConfig.PAD_INNER < 0) missing.append(" ui.pad.inner");
                if (UiConfig.BTN_W < 0) missing.append(" ui.btn.w");
                if (UiConfig.BTN_H < 0) missing.append(" ui.btn.h");
                if (UiConfig.TABLE_ROW_H < 0) missing.append(" ui.table.row.h");

                if (UiConfig.PRIMARY_TEXT == null) missing.append(" ui.color.primary.text");
                if (UiConfig.TABLE_HEADER_BG == null) missing.append(" ui.color.table.header.bg");
                if (UiConfig.TABLE_BG == null) missing.append(" ui.color.table.bg");
                if (UiConfig.TABLE_HEADER_FG == null) missing.append(" ui.color.table.header.fg");
                if (UiConfig.TABLE_GRID == null) missing.append(" ui.color.table.grid");
                if (UiConfig.BTN_ICON_W < 0) missing.append(" ui.btn.icon.w");
                if (UiConfig.BTN_ICON_H < 0) missing.append(" ui.btn.icon.h");

                if (!missing.isEmpty()) {
                    throw new IllegalStateException("UI nastavitve niso pravilno naložene. Manjka:" + missing);
                }
            }
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
        try (PreparedStatement ps = db.getConnection().prepareStatement("SELECT * FROM get_all_employees()");
             ResultSet rs = ps.executeQuery()) {

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("priimek"),
                        rs.getString("delovno_mesto"),
                        rs.getString("oddelek"),
                        rs.getFloat("placa"),
                        rs.getDate("datum_zaposlitve")
                });
            }
        }
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

        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT add_employee(?, ?, ?, ?, ?, ?::date, ?, ?, ?, ?)"
        )) {
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

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public ResultSet getDelovnaMesta() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_delovna_mesta()"
        );
        return ps.executeQuery();
    }

    public ResultSet getOddelki(int delovnoMestoId) throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM public.get_oddelki(?)"
        );
        ps.setInt(1, delovnoMestoId);
        return ps.executeQuery();
    }

    public ResultSet getKraji() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_kraji()"
        );
        return ps.executeQuery();
    }

    public ResultSet getIzobrazba() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_izobrazba()"
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
            int krajId,
            int izobrazbaId
    ) throws Exception {

        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT update_employee(?, ?, ?, ?, ?, ?::numeric, ?::date, ?, ?, ?, ?)"
        )) {
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
            ps.setInt(11, izobrazbaId);

            ps.execute();
        }
    }

    public void deleteEmployee(int employeeId) throws Exception {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT delete_employee(?)"
        )) {
            ps.setInt(1, employeeId);
            ps.execute();
        }
    }

    public void refreshApp() throws Exception {
        loadUiConfig();
    }
}
