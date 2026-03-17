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
        UiConfig.FONT_H3 = null;
        UiConfig.FONT_SMALL = null;

        UiConfig.FONT_BASE_BOLD = null;
        UiConfig.FONT_H1_BOLD = null;
        UiConfig.FONT_H2_BOLD = null;
        UiConfig.FONT_SMALL_BOLD = null;

        UiConfig.BG_APP = null;
        UiConfig.BG_BAR = null;
        UiConfig.BG_CARD = null;
        UiConfig.BG_CARD_DARK = null;

        UiConfig.BORDER = null;
        UiConfig.BORDER_BOLD = null;
        UiConfig.BORDER_ACCENT = null;

        UiConfig.TEXT = null;
        UiConfig.TEXT_BOLD = null;
        UiConfig.TEXT_MUTED = null;
        UiConfig.TEXT_ACCENT = null;

        UiConfig.PRIMARY = null;
        UiConfig.PRIMARY_DARK = null;
        UiConfig.PRIMARY_LIGHT = null;
        UiConfig.PRIMARY_TEXT = null;

        UiConfig.SUCCESS = null;
        UiConfig.SUCCESS_DARK = null;
        UiConfig.DANGER = null;
        UiConfig.DANGER_DARK = null;

        UiConfig.WARNING = null;
        UiConfig.INFO = null;

        UiConfig.TABLE_BG = null;
        UiConfig.TABLE_BG_ALT = null;
        UiConfig.TABLE_HEADER_BG = null;
        UiConfig.TABLE_HEADER_BG_DARK = null;
        UiConfig.TABLE_HEADER_FG = null;
        UiConfig.TABLE_GRID = null;
        UiConfig.TABLE_ROW_HOVER = null;
        UiConfig.TABLE_ROW_SELECTED = null;

        UiConfig.INPUT_BG = null;
        UiConfig.INPUT_BORDER = null;
        UiConfig.INPUT_BORDER_FOCUS = null;
        UiConfig.TEXT_PLACEHOLDER = null;

        UiConfig.SHADOW = null;
        UiConfig.OVERLAY = null;

        UiConfig.PAD_SMALL = -1;
        UiConfig.PAD = -1;
        UiConfig.PAD_LARGE = -1;
        UiConfig.PAD_XLARGE = -1;

        UiConfig.PAD_INNER_SMALL = -1;
        UiConfig.PAD_INNER = -1;
        UiConfig.PAD_INNER_LARGE = -1;

        UiConfig.GAP_SMALL = -1;
        UiConfig.GAP = -1;
        UiConfig.GAP_LARGE = -1;

        UiConfig.BTN_W_SMALL = -1;
        UiConfig.BTN_W = -1;
        UiConfig.BTN_W_LARGE = -1;

        UiConfig.BTN_H_SMALL = -1;
        UiConfig.BTN_H = -1;
        UiConfig.BTN_H_LARGE = -1;

        UiConfig.BTN_RADIUS_SMALL = -1;
        UiConfig.BTN_RADIUS = -1;
        UiConfig.BTN_RADIUS_LARGE = -1;

        UiConfig.BTN_ICON_W = -1;
        UiConfig.BTN_ICON_H = -1;

        UiConfig.INPUT_H = -1;
        UiConfig.INPUT_RADIUS = -1;

        UiConfig.TABLE_ROW_H = -1;
        UiConfig.TABLE_ROW_H_LARGE = -1;

        UiConfig.BORDER_WIDTH_THIN = -1;
        UiConfig.BORDER_WIDTH = -1;
        UiConfig.BORDER_WIDTH_THICK = -1;

        UiConfig.SHADOW_BLUR_SMALL = -1;
        UiConfig.SHADOW_BLUR = -1;
        UiConfig.SHADOW_BLUR_LARGE = -1;

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
                Integer fontH3Size = null;
                Integer fontSmallSize = null;

                while (rs.next()) {
                    String k = rs.getString("name");
                    String v = rs.getString("value");

                    switch (k) {
                        case "ui.font.family"     -> fontFamily = v;
                        case "ui.font.size.base"  -> fontBaseSize = Integer.parseInt(v);
                        case "ui.font.size.h1"    -> fontH1Size = Integer.parseInt(v);
                        case "ui.font.size.h2"    -> fontH2Size = Integer.parseInt(v);
                        case "ui.font.size.h3"    -> fontH3Size = Integer.parseInt(v);
                        case "ui.font.size.small" -> fontSmallSize = Integer.parseInt(v);

                        case "ui.color.bg.app"         -> UiConfig.BG_APP = Color.decode(v);
                        case "ui.color.bg.bar"         -> UiConfig.BG_BAR = Color.decode(v);
                        case "ui.color.bg.card"        -> UiConfig.BG_CARD = Color.decode(v);
                        case "ui.color.bg.card.dark"   -> UiConfig.BG_CARD_DARK = Color.decode(v);

                        case "ui.color.border"         -> UiConfig.BORDER = Color.decode(v);
                        case "ui.color.border.bold"    -> UiConfig.BORDER_BOLD = Color.decode(v);
                        case "ui.color.border.accent"  -> UiConfig.BORDER_ACCENT = Color.decode(v);

                        case "ui.color.text"           -> UiConfig.TEXT = Color.decode(v);
                        case "ui.color.text.bold"      -> UiConfig.TEXT_BOLD = Color.decode(v);
                        case "ui.color.text.muted"     -> UiConfig.TEXT_MUTED = Color.decode(v);
                        case "ui.color.text.accent"    -> UiConfig.TEXT_ACCENT = Color.decode(v);

                        case "ui.color.primary"        -> UiConfig.PRIMARY = Color.decode(v);
                        case "ui.color.primary.dark"   -> UiConfig.PRIMARY_DARK = Color.decode(v);
                        case "ui.color.primary.light"  -> UiConfig.PRIMARY_LIGHT = Color.decode(v);
                        case "ui.color.primary.text"   -> UiConfig.PRIMARY_TEXT = Color.decode(v);

                        case "ui.color.success"        -> UiConfig.SUCCESS = Color.decode(v);
                        case "ui.color.success.dark"   -> UiConfig.SUCCESS_DARK = Color.decode(v);
                        case "ui.color.danger"         -> UiConfig.DANGER = Color.decode(v);
                        case "ui.color.danger.dark"    -> UiConfig.DANGER_DARK = Color.decode(v);

                        case "ui.color.warning"        -> UiConfig.WARNING = Color.decode(v);
                        case "ui.color.info"           -> UiConfig.INFO = Color.decode(v);

                        case "ui.color.table.bg"           -> UiConfig.TABLE_BG = Color.decode(v);
                        case "ui.color.table.bg.alt"       -> UiConfig.TABLE_BG_ALT = Color.decode(v);
                        case "ui.color.table.header.bg"    -> UiConfig.TABLE_HEADER_BG = Color.decode(v);
                        case "ui.color.table.header.bg.dark" -> UiConfig.TABLE_HEADER_BG_DARK = Color.decode(v);
                        case "ui.color.table.header.fg"    -> UiConfig.TABLE_HEADER_FG = Color.decode(v);
                        case "ui.color.table.grid"         -> UiConfig.TABLE_GRID = Color.decode(v);
                        case "ui.color.table.row.hover"    -> UiConfig.TABLE_ROW_HOVER = Color.decode(v);
                        case "ui.color.table.row.selected" -> UiConfig.TABLE_ROW_SELECTED = Color.decode(v);

                        case "ui.color.input.bg"           -> UiConfig.INPUT_BG = Color.decode(v);
                        case "ui.color.input.border"       -> UiConfig.INPUT_BORDER = Color.decode(v);
                        case "ui.color.input.border.focus" -> UiConfig.INPUT_BORDER_FOCUS = Color.decode(v);
                        case "ui.color.text.placeholder"   -> UiConfig.TEXT_PLACEHOLDER = Color.decode(v);

                        case "ui.color.shadow"             -> UiConfig.SHADOW = Color.decode(v);
                        case "ui.color.overlay"            -> UiConfig.OVERLAY = Color.decode(v);

                        case "ui.pad.small"    -> UiConfig.PAD_SMALL = Integer.parseInt(v);
                        case "ui.pad"          -> UiConfig.PAD = Integer.parseInt(v);
                        case "ui.pad.large"    -> UiConfig.PAD_LARGE = Integer.parseInt(v);
                        case "ui.pad.xlarge"   -> UiConfig.PAD_XLARGE = Integer.parseInt(v);

                        case "ui.pad.inner.small"  -> UiConfig.PAD_INNER_SMALL = Integer.parseInt(v);
                        case "ui.pad.inner"        -> UiConfig.PAD_INNER = Integer.parseInt(v);
                        case "ui.pad.inner.large"  -> UiConfig.PAD_INNER_LARGE = Integer.parseInt(v);

                        case "ui.gap.small"    -> UiConfig.GAP_SMALL = Integer.parseInt(v);
                        case "ui.gap"          -> UiConfig.GAP = Integer.parseInt(v);
                        case "ui.gap.large"    -> UiConfig.GAP_LARGE = Integer.parseInt(v);

                        case "ui.btn.w.small"  -> UiConfig.BTN_W_SMALL = Integer.parseInt(v);
                        case "ui.btn.w"        -> UiConfig.BTN_W = Integer.parseInt(v);
                        case "ui.btn.w.large"  -> UiConfig.BTN_W_LARGE = Integer.parseInt(v);

                        case "ui.btn.h.small"  -> UiConfig.BTN_H_SMALL = Integer.parseInt(v);
                        case "ui.btn.h"        -> UiConfig.BTN_H = Integer.parseInt(v);
                        case "ui.btn.h.large"  -> UiConfig.BTN_H_LARGE = Integer.parseInt(v);

                        case "ui.btn.radius.small" -> UiConfig.BTN_RADIUS_SMALL = Integer.parseInt(v);
                        case "ui.btn.radius"       -> UiConfig.BTN_RADIUS = Integer.parseInt(v);
                        case "ui.btn.radius.large" -> UiConfig.BTN_RADIUS_LARGE = Integer.parseInt(v);

                        case "ui.btn.icon.w" -> UiConfig.BTN_ICON_W = Integer.parseInt(v);
                        case "ui.btn.icon.h" -> UiConfig.BTN_ICON_H = Integer.parseInt(v);

                        case "ui.input.h"       -> UiConfig.INPUT_H = Integer.parseInt(v);
                        case "ui.input.radius"  -> UiConfig.INPUT_RADIUS = Integer.parseInt(v);

                        case "ui.table.row.h"       -> UiConfig.TABLE_ROW_H = Integer.parseInt(v);
                        case "ui.table.row.h.large" -> UiConfig.TABLE_ROW_H_LARGE = Integer.parseInt(v);

                        case "ui.border.width.thin"   -> UiConfig.BORDER_WIDTH_THIN = Integer.parseInt(v);
                        case "ui.border.width"        -> UiConfig.BORDER_WIDTH = Integer.parseInt(v);
                        case "ui.border.width.thick"  -> UiConfig.BORDER_WIDTH_THICK = Integer.parseInt(v);

                        case "ui.shadow.blur.small" -> UiConfig.SHADOW_BLUR_SMALL = Integer.parseInt(v);
                        case "ui.shadow.blur"       -> UiConfig.SHADOW_BLUR = Integer.parseInt(v);
                        case "ui.shadow.blur.large" -> UiConfig.SHADOW_BLUR_LARGE = Integer.parseInt(v);

                        default -> {
                        }
                    }
                }

                if (fontFamily != null && fontBaseSize != null && fontH1Size != null &&
                        fontH2Size != null && fontH3Size != null && fontSmallSize != null) {

                    UiConfig.FONT_BASE = new Font(fontFamily, Font.PLAIN, fontBaseSize);
                    UiConfig.FONT_H1   = new Font(fontFamily, Font.BOLD, fontH1Size);
                    UiConfig.FONT_H2   = new Font(fontFamily, Font.BOLD, fontH2Size);
                    UiConfig.FONT_H3   = new Font(fontFamily, Font.BOLD, fontH3Size);
                    UiConfig.FONT_SMALL = new Font(fontFamily, Font.PLAIN, fontSmallSize);

                    UiConfig.FONT_BASE_BOLD = new Font(fontFamily, Font.BOLD, fontBaseSize);
                    UiConfig.FONT_H1_BOLD   = new Font(fontFamily, Font.BOLD, fontH1Size + 2);
                    UiConfig.FONT_H2_BOLD   = new Font(fontFamily, Font.BOLD, fontH2Size + 1);
                    UiConfig.FONT_SMALL_BOLD = new Font(fontFamily, Font.BOLD, fontSmallSize);
                }

                StringBuilder missing = new StringBuilder();

                if (UiConfig.FONT_BASE == null) {
                    UiConfig.FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
                    UiConfig.FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
                    UiConfig.FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
                    UiConfig.FONT_H3 = new Font("Segoe UI", Font.BOLD, 16);
                    UiConfig.FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

                    UiConfig.FONT_BASE_BOLD = new Font("Segoe UI", Font.BOLD, 14);
                    UiConfig.FONT_H1_BOLD = new Font("Segoe UI", Font.BOLD, 26);
                    UiConfig.FONT_H2_BOLD = new Font("Segoe UI", Font.BOLD, 20);
                    UiConfig.FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
                }

                if (UiConfig.PAD < 0) UiConfig.PAD = 20;
                if (UiConfig.PAD_SMALL < 0) UiConfig.PAD_SMALL = 10;
                if (UiConfig.PAD_LARGE < 0) UiConfig.PAD_LARGE = 30;
                if (UiConfig.PAD_XLARGE < 0) UiConfig.PAD_XLARGE = 40;

                if (UiConfig.PAD_INNER < 0) UiConfig.PAD_INNER = 15;
                if (UiConfig.PAD_INNER_SMALL < 0) UiConfig.PAD_INNER_SMALL = 8;
                if (UiConfig.PAD_INNER_LARGE < 0) UiConfig.PAD_INNER_LARGE = 20;

                if (UiConfig.GAP < 0) UiConfig.GAP = 12;
                if (UiConfig.GAP_SMALL < 0) UiConfig.GAP_SMALL = 6;
                if (UiConfig.GAP_LARGE < 0) UiConfig.GAP_LARGE = 18;

                if (UiConfig.BORDER_WIDTH < 0) UiConfig.BORDER_WIDTH = 2;
                if (UiConfig.BORDER_WIDTH_THIN < 0) UiConfig.BORDER_WIDTH_THIN = 1;
                if (UiConfig.BORDER_WIDTH_THICK < 0) UiConfig.BORDER_WIDTH_THICK = 3;

                if (UiConfig.BTN_RADIUS < 0) UiConfig.BTN_RADIUS = 6;
                if (UiConfig.BTN_RADIUS_SMALL < 0) UiConfig.BTN_RADIUS_SMALL = 4;
                if (UiConfig.BTN_RADIUS_LARGE < 0) UiConfig.BTN_RADIUS_LARGE = 8;

                if (UiConfig.INPUT_RADIUS < 0) UiConfig.INPUT_RADIUS = 5;
                if (UiConfig.INPUT_H < 0) UiConfig.INPUT_H = 38;

                if (UiConfig.BTN_W < 0) UiConfig.BTN_W = 140;
                if (UiConfig.BTN_H < 0) UiConfig.BTN_H = 42;
                if (UiConfig.BTN_W_SMALL < 0) UiConfig.BTN_W_SMALL = 100;
                if (UiConfig.BTN_H_SMALL < 0) UiConfig.BTN_H_SMALL = 36;
                if (UiConfig.BTN_W_LARGE < 0) UiConfig.BTN_W_LARGE = 180;
                if (UiConfig.BTN_H_LARGE < 0) UiConfig.BTN_H_LARGE = 48;

                if (UiConfig.TABLE_ROW_H < 0) UiConfig.TABLE_ROW_H = 40;
                if (UiConfig.TABLE_ROW_H_LARGE < 0) UiConfig.TABLE_ROW_H_LARGE = 48;

                if (UiConfig.BTN_ICON_W < 0) UiConfig.BTN_ICON_W = 40;
                if (UiConfig.BTN_ICON_H < 0) UiConfig.BTN_ICON_H = 40;

                if (UiConfig.SHADOW_BLUR < 0) UiConfig.SHADOW_BLUR = 10;
                if (UiConfig.SHADOW_BLUR_SMALL < 0) UiConfig.SHADOW_BLUR_SMALL = 5;
                if (UiConfig.SHADOW_BLUR_LARGE < 0) UiConfig.SHADOW_BLUR_LARGE = 15;

                if (UiConfig.BG_APP == null) UiConfig.BG_APP = new Color(245, 247, 250);
                if (UiConfig.BG_BAR == null) UiConfig.BG_BAR = new Color(255, 255, 255);
                if (UiConfig.BG_CARD == null) UiConfig.BG_CARD = new Color(255, 255, 255);
                if (UiConfig.BG_CARD_DARK == null) UiConfig.BG_CARD_DARK = new Color(248, 249, 252);

                if (UiConfig.BORDER == null) UiConfig.BORDER = new Color(225, 230, 239);
                if (UiConfig.BORDER_BOLD == null) UiConfig.BORDER_BOLD = new Color(200, 210, 225);
                if (UiConfig.BORDER_ACCENT == null) UiConfig.BORDER_ACCENT = new Color(52, 152, 219);

                if (UiConfig.TEXT == null) UiConfig.TEXT = new Color(52, 73, 94);
                if (UiConfig.TEXT_BOLD == null) UiConfig.TEXT_BOLD = new Color(44, 62, 80);
                if (UiConfig.TEXT_MUTED == null) UiConfig.TEXT_MUTED = new Color(127, 140, 153);
                if (UiConfig.TEXT_ACCENT == null) UiConfig.TEXT_ACCENT = new Color(41, 128, 185);

                if (UiConfig.PRIMARY == null) UiConfig.PRIMARY = new Color(52, 152, 219);
                if (UiConfig.PRIMARY_DARK == null) UiConfig.PRIMARY_DARK = new Color(41, 128, 185);
                if (UiConfig.PRIMARY_LIGHT == null) UiConfig.PRIMARY_LIGHT = new Color(93, 173, 226);
                if (UiConfig.PRIMARY_TEXT == null) UiConfig.PRIMARY_TEXT = Color.WHITE;

                if (UiConfig.SUCCESS == null) UiConfig.SUCCESS = new Color(46, 204, 113);
                if (UiConfig.SUCCESS_DARK == null) UiConfig.SUCCESS_DARK = new Color(39, 174, 96);
                if (UiConfig.DANGER == null) UiConfig.DANGER = new Color(231, 76, 60);
                if (UiConfig.DANGER_DARK == null) UiConfig.DANGER_DARK = new Color(192, 57, 43);

                if (UiConfig.WARNING == null) UiConfig.WARNING = new Color(241, 196, 15);
                if (UiConfig.INFO == null) UiConfig.INFO = new Color(52, 152, 219);

                if (UiConfig.TABLE_BG == null) UiConfig.TABLE_BG = Color.WHITE;
                if (UiConfig.TABLE_BG_ALT == null) UiConfig.TABLE_BG_ALT = new Color(250, 251, 252);
                if (UiConfig.TABLE_HEADER_BG == null) UiConfig.TABLE_HEADER_BG = new Color(248, 249, 252);
                if (UiConfig.TABLE_HEADER_BG_DARK == null) UiConfig.TABLE_HEADER_BG_DARK = new Color(241, 242, 246);
                if (UiConfig.TABLE_HEADER_FG == null) UiConfig.TABLE_HEADER_FG = new Color(52, 73, 94);
                if (UiConfig.TABLE_GRID == null) UiConfig.TABLE_GRID = new Color(225, 230, 239);
                if (UiConfig.TABLE_ROW_HOVER == null) UiConfig.TABLE_ROW_HOVER = new Color(248, 251, 255);
                if (UiConfig.TABLE_ROW_SELECTED == null) UiConfig.TABLE_ROW_SELECTED = new Color(235, 245, 255);

                if (UiConfig.INPUT_BG == null) UiConfig.INPUT_BG = Color.WHITE;
                if (UiConfig.INPUT_BORDER == null) UiConfig.INPUT_BORDER = new Color(225, 230, 239);
                if (UiConfig.INPUT_BORDER_FOCUS == null) UiConfig.INPUT_BORDER_FOCUS = new Color(52, 152, 219);
                if (UiConfig.TEXT_PLACEHOLDER == null) UiConfig.TEXT_PLACEHOLDER = new Color(170, 178, 189);

                if (UiConfig.SHADOW == null) UiConfig.SHADOW = new Color(0, 0, 0, 30);
                if (UiConfig.OVERLAY == null) UiConfig.OVERLAY = new Color(0, 0, 0, 50);

            }
        }
    }

    private void showLoginWindow() {
        loginWindow = new LoginWindow(this);
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
            String[] userData = authService.login(username, password);

            if (userData != null) {
                String userEmail = userData[1];

                loginWindow.dispose();
                loadUiConfig();
                //new MainWindow(this, userEmail, username);
            } else {
                JOptionPane.showMessageDialog(loginWindow,
                        "Napačno uporabniško ime ali geslo.\n\n" +
                                "Prosimo obrnite se na support:\n" +
                                "blaz.kristan.bk@gmail.com",
                        "Napaka pri prijavi",
                        JOptionPane.ERROR_MESSAGE);
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
                    rs.getString(8),
                    rs.getString(9),
                    rs.getFloat(6),
                    rs.getDate(7),
                    rs.getString(4),
                    rs.getString(10),
                    rs.getString(11)
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

    public boolean registerAdmin(String username, String password, String email) throws Exception {
        return authService.register(username, password, email);
    }

    public ResultSet getDelovnaMesta() throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_delovna_mesta()"
        );
        return ps.executeQuery();
    }

    public ResultSet getOddelki(int delovnoMestoId) throws Exception {
        PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM get_oddelki(?)"
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

    public void refreshApp() throws Exception {
        loadUiConfig();
    }
    public AuthService getAuthService() {
        return authService;
    }
}