package ui;

import controller.AppController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;

public class MainWindow extends JFrame {

    private final AppController controller;

    private final JTable employeeTable;

    // UI references
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel topPanel;
    private JPanel buttonBar;
    private JPanel userInfoPanel; // DODANO: panel za uporabniške informacije

    private JLabel lblDashboard;
    private JLabel titleLabel;
    private JLabel lblUserEmail; // DODANO: email uporabnika
    private JLabel lblUsername;  // DODANO: username uporabnika

    private JScrollPane tableScroll;

    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnLogout; // DODANO: logout gumb

    // DODANO: spremenljivki za uporabniške podatke
    private String currentUserEmail;
    private String currentUsername;

    // SPREMENJENO: samo en konstruktor, ki sprejema vse podatke
    public MainWindow(AppController controller, String email, String username) {
        this.controller = controller;
        this.currentUserEmail = email;
        this.currentUsername = username;

        setTitle("EZP – Evidenca zaposlenih podjetja");
        setSize(1100, 650); // POVEČANA ŠIRINA za user info
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        employeeTable = createEmployeeTable();
        setContentPane(buildUi());
        refreshTable();

        setVisible(true);
    }

    private JPanel buildUi() {
        mainPanel = new JPanel(new BorderLayout());
        headerPanel = new JPanel();
        topPanel = new JPanel(new BorderLayout());
        tableScroll = new JScrollPane(employeeTable);

        // DODANO: User info panel (zgoraj levo)
        userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.X_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(5, 10, 5, 20));

        // DODANO: Logout gumb z ikono
        btnLogout = new JButton();
        btnLogout.setToolTipText("Odjava");
        btnLogout.setPreferredSize(new Dimension(50, 50));
        btnLogout.setMinimumSize(new Dimension(50, 50));
        btnLogout.setMaximumSize(new Dimension(50, 50));
        btnLogout.setBorder(BorderFactory.createEmptyBorder());
        btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Poskusite naložiti ikono
        try {
            ImageIcon logoutIcon = new ImageIcon("leave.png");
            if (logoutIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = logoutIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                btnLogout.setIcon(new ImageIcon(img));
            } else {
                // Če ikona ne obstaja, uporabite text
                btnLogout.setText("X");
                btnLogout.setFont(UiConfig.FONT_H2);
                btnLogout.setForeground(UiConfig.DANGER);
            }
        } catch (Exception e) {
            btnLogout.setText("🚪");
            btnLogout.setFont(UiConfig.FONT_H2);
        }

        btnLogout.addActionListener(e -> onLogout());

        // DODANO: Uporabniški podatki
        JPanel userTextPanel = new JPanel();
        userTextPanel.setOpaque(false);
        userTextPanel.setLayout(new BoxLayout(userTextPanel, BoxLayout.Y_AXIS));
        userTextPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        lblUsername = new JLabel(currentUsername);
        lblUsername.setFont(UiConfig.FONT_H2);
        lblUsername.setForeground(UiConfig.TEXT);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblUserEmail = new JLabel(currentUserEmail);
        lblUserEmail.setFont(UiConfig.FONT_SMALL);
        lblUserEmail.setForeground(UiConfig.TEXT_MUTED);
        lblUserEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        userTextPanel.add(lblUsername);
        userTextPanel.add(Box.createVerticalStrut(2));
        userTextPanel.add(lblUserEmail);

        userInfoPanel.add(userTextPanel);
        userInfoPanel.add(Box.createHorizontalStrut(15));
        userInfoPanel.add(btnLogout);

        // header (dashboard) - premaknjeno desno
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setOpaque(false);
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        lblDashboard = new JLabel("DASHBOARD");
        lblDashboard.setFont(UiConfig.FONT_H1);
        lblDashboard.setForeground(UiConfig.TEXT_MUTED);
        lblDashboard.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardPanel.add(lblDashboard);

        headerPanel.add(userInfoPanel, BorderLayout.WEST); // user info levo
        headerPanel.add(dashboardPanel, BorderLayout.CENTER); // dashboard v sredini

        // top bar
        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(true);
        topPanel.setBackground(UiConfig.BG_BAR);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, UiConfig.BORDER),
                new EmptyBorder(8, 15, 8, 15)
        ));

        titleLabel = new JLabel("Seznam zaposlenih");
        titleLabel.setFont(UiConfig.FONT_H1);
        titleLabel.setForeground(UiConfig.TEXT);

        buttonBar = buildButtonBar();
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonBar, BorderLayout.EAST);

        // scrollpane
        tableScroll.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        // north wrapper
        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(headerPanel);
        north.add(Box.createVerticalStrut(5));
        north.add(topPanel);

        mainPanel.add(north, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        applyUi(); // initial style apply

        return mainPanel;
    }

    private JPanel buildButtonBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        btnRefresh = button("⟳", UiConfig.BG_BAR, UiConfig.TEXT_MUTED,
                UiConfig.BTN_H, UiConfig.BTN_H, this::refreshTable);

        btnAdd = button("+ Dodaj zaposlenega", UiConfig.PRIMARY, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::onAdd);

        btnEdit = button("Uredi", UiConfig.SUCCESS, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::onEdit);

        btnDelete = button("X Izbriši", UiConfig.DANGER, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::onDelete);

        p.add(btnRefresh);
        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);

        return p;
    }

    private JButton button(String text, Color bg, Color fg, int w, int h, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(w, h));

        b.setFont(UiConfig.FONT_BASE);
        b.setBackground(bg);
        b.setForeground(fg);

        return b;
    }

    private JTable createEmployeeTable() {
        String[] columns = {"ID", "Ime", "Priimek", "Delovno mesto", "Oddelek", "Plača", "Datum zaposlitve"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable t = new JTable(model);

        t.setRowHeight(UiConfig.TABLE_ROW_H);
        t.setFont(UiConfig.FONT_BASE);
        t.setBackground(UiConfig.BG_CARD);
        t.setForeground(UiConfig.TEXT);
        t.setGridColor(UiConfig.TABLE_GRID);

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(UiConfig.TABLE_HEADER_BG);
        header.setForeground(UiConfig.TABLE_HEADER_FG);
        header.setFont(UiConfig.FONT_H2);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setComparator(5, Comparator.comparingDouble(v -> {
            if (v == null) return 0.0;
            String s = v.toString()
                    .replace(".", "")
                    .replace(",", ".")
                    .replaceAll("[^0-9.]", "");
            if (s.isBlank()) return 0.0;
            return Double.parseDouble(s);
        }));

        t.setRowSorter(sorter);

        return t;
    }

    private void applyUi() {
        getContentPane().setBackground(UiConfig.BG_APP);

        if (mainPanel != null) {
            mainPanel.setBackground(UiConfig.BG_APP);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(
                    UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD
            ));
        }

        if (lblDashboard != null) {
            lblDashboard.setFont(UiConfig.FONT_H1);
            lblDashboard.setForeground(UiConfig.TEXT_MUTED);
        }

        if (topPanel != null) {
            topPanel.setBackground(UiConfig.BG_BAR);
        }
        if (titleLabel != null) {
            titleLabel.setFont(UiConfig.FONT_H1);
            titleLabel.setForeground(UiConfig.TEXT);
        }

        if (lblUsername != null) {
            lblUsername.setFont(UiConfig.FONT_H2);
            lblUsername.setForeground(UiConfig.TEXT);
        }
        if (lblUserEmail != null) {
            lblUserEmail.setFont(UiConfig.FONT_SMALL);
            lblUserEmail.setForeground(UiConfig.TEXT_MUTED);
        }

        restyleButton(btnRefresh, UiConfig.BG_BAR, UiConfig.TEXT_MUTED, UiConfig.BTN_H, UiConfig.BTN_H);
        restyleButton(btnAdd, UiConfig.PRIMARY, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);
        restyleButton(btnEdit, UiConfig.SUCCESS, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);
        restyleButton(btnDelete, UiConfig.DANGER, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);

        if (btnLogout != null) {
            btnLogout.setFont(UiConfig.FONT_H2);
            btnLogout.setForeground(UiConfig.DANGER);
            btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        if (tableScroll != null) {
            tableScroll.getViewport().setBackground(UiConfig.BG_CARD);
            tableScroll.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));
        }

        if (employeeTable != null) {
            employeeTable.setRowHeight(UiConfig.TABLE_ROW_H);
            employeeTable.setFont(UiConfig.FONT_BASE);
            employeeTable.setBackground(UiConfig.BG_CARD);
            employeeTable.setForeground(UiConfig.TEXT);
            employeeTable.setGridColor(UiConfig.TABLE_GRID);
            employeeTable.setBackground(UiConfig.TABLE_BG);

            JTableHeader th = employeeTable.getTableHeader();
            if (th != null) {
                th.setBackground(UiConfig.TABLE_HEADER_BG);
                th.setForeground(UiConfig.TABLE_HEADER_FG);
                th.setFont(UiConfig.FONT_H2);
                th.setReorderingAllowed(false);
            }
        }
    }

    private void restyleButton(JButton b, Color bg, Color fg, int w, int h) {
        if (b == null) return;
        b.setFont(UiConfig.FONT_BASE);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setPreferredSize(new Dimension(w, h));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Ali ste prepričani, da se želite odjaviti?",
                "Potrdi odjavo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginWindow(controller);
            //.exit(0); // Za zdaj samo zapremo
        }
    }

    private void refreshTable() {
        try {
            if (employeeTable.getRowSorter() != null) {
                employeeTable.getRowSorter().setSortKeys(null);
            }

            controller.refreshApp();
            applyUi();
            controller.loadEmployees((DefaultTableModel) employeeTable.getModel());

            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        AddEmployee dlg = new AddEmployee(this, controller);
        dlg.setVisible(true);
        refreshTable();
    }

    private void onEdit() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        int row = employeeTable.convertRowIndexToModel(viewRow);
        int employeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());

        AddEmployee dlg = new AddEmployee(this, controller, employeeId);
        dlg.setVisible(true);
        refreshTable();
    }

    private void onDelete() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        int row = employeeTable.convertRowIndexToModel(viewRow);
        int employeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Izbrišem?", "Potrdi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteEmployee(employeeId);
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }
}