package ui;

import controller.AppController;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;

public class MainWindow extends JFrame {

    private final AppController controller;
    private final JTable employeeTable;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel topPanel;
    private JPanel buttonBar;
    private JPanel userInfoPanel;

    private JLabel lblDashboard;
    private JLabel titleLabel;
    private JLabel lblUserEmail;
    private JLabel lblUsername;

    private JScrollPane tableScroll;

    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnLogout;

    private String currentUserEmail;
    private String currentUsername;

    public MainWindow(AppController controller, String email, String username) {
        this.controller = controller;
        this.currentUserEmail = email;
        this.currentUsername = username;

        setTitle("EZP – EVIDENCA ZAPOSLENIH PODJETJA");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        employeeTable = createEmployeeTable();
        setContentPane(buildUi());
        addMouseWheelZoom();
        refreshTable();

        setVisible(true);
    }

    private void addMouseWheelZoom() {
        employeeTable.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int currentHeight = employeeTable.getRowHeight();
                    int newHeight = currentHeight - e.getWheelRotation() * 2;

                    if (newHeight >= 20 && newHeight <= 60) {
                        employeeTable.setRowHeight(newHeight);
                        tableScroll.revalidate();
                        tableScroll.repaint();
                    }
                    e.consume();
                }
            }
        });

        tableScroll.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int currentHeight = employeeTable.getRowHeight();
                    int newHeight = currentHeight - e.getWheelRotation() * 2;

                    if (newHeight >= 20 && newHeight <= 60) {
                        employeeTable.setRowHeight(newHeight);
                        tableScroll.revalidate();
                        tableScroll.repaint();
                    }
                    e.consume();
                }
            }
        });

        KeyStroke zoomIn = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK);
        KeyStroke zoomOut = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK);

        employeeTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(zoomIn, "zoomIn");
        employeeTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(zoomOut, "zoomOut");

        employeeTable.getActionMap().put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentHeight = employeeTable.getRowHeight();
                int newHeight = currentHeight + 2;
                if (newHeight <= 60) {
                    employeeTable.setRowHeight(newHeight);
                }
            }
        });

        employeeTable.getActionMap().put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentHeight = employeeTable.getRowHeight();
                int newHeight = currentHeight - 2;
                if (newHeight >= 20) {
                    employeeTable.setRowHeight(newHeight);
                }
            }
        });
    }

    private JPanel buildUi() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UiConfig.BG_APP);
        mainPanel.setBorder(new EmptyBorder(UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD));

        userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.X_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD, UiConfig.PAD_INNER, UiConfig.PAD));

        btnLogout = new JButton("ODJAVA");
        btnLogout.setFont(UiConfig.FONT_BASE_BOLD);
        btnLogout.setForeground(UiConfig.DANGER);
        btnLogout.setBackground(UiConfig.BG_CARD);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiConfig.DANGER, UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(true);
        btnLogout.setMinimumSize(new Dimension(UiConfig.BTN_W, UiConfig.BTN_H));

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(UiConfig.DANGER);
                btnLogout.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(UiConfig.BG_CARD);
                btnLogout.setForeground(UiConfig.DANGER);
            }
        });

        btnLogout.addActionListener(e -> onLogout());

        JPanel userTextPanel = new JPanel();
        userTextPanel.setOpaque(false);
        userTextPanel.setLayout(new BoxLayout(userTextPanel, BoxLayout.Y_AXIS));
        userTextPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        lblUsername = new JLabel(currentUsername.toUpperCase());
        lblUsername.setFont(UiConfig.FONT_H2_BOLD);
        lblUsername.setForeground(UiConfig.TEXT_BOLD);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblUserEmail = new JLabel(currentUserEmail);
        lblUserEmail.setFont(UiConfig.FONT_SMALL_BOLD);
        lblUserEmail.setForeground(UiConfig.PRIMARY);
        lblUserEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        userTextPanel.add(lblUsername);
        userTextPanel.add(Box.createVerticalStrut(UiConfig.GAP_SMALL));
        userTextPanel.add(lblUserEmail);

        userInfoPanel.add(userTextPanel);
        userInfoPanel.add(Box.createHorizontalStrut(UiConfig.GAP_LARGE));
        userInfoPanel.add(btnLogout);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, UiConfig.PAD, 0));

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setOpaque(false);
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        lblDashboard = new JLabel("DASHBOARD");
        lblDashboard.setFont(UiConfig.FONT_H1_BOLD);
        lblDashboard.setForeground(UiConfig.TEXT_ACCENT);
        lblDashboard.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardPanel.add(lblDashboard);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(dashboardPanel, BorderLayout.CENTER);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(true);
        topPanel.setBackground(UiConfig.BG_BAR);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(UiConfig.BORDER_WIDTH, 0, UiConfig.BORDER_WIDTH, 0, UiConfig.BORDER_BOLD),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_LARGE, UiConfig.PAD_INNER, UiConfig.PAD_LARGE)
        ));

        titleLabel = new JLabel("SEZNAM ZAPOSLENIH");
        titleLabel.setFont(UiConfig.FONT_H2_BOLD);
        titleLabel.setForeground(UiConfig.TEXT_BOLD);

        buttonBar = buildButtonBar();
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonBar, BorderLayout.EAST);

        employeeTable.setRowHeight(UiConfig.TABLE_ROW_H);
        tableScroll = new JScrollPane(employeeTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER_BOLD, UiConfig.BORDER_WIDTH));
        tableScroll.getViewport().setBackground(UiConfig.TABLE_BG);

        JLabel zoomHint = new JLabel("💡 CTRL + SCROLL za spreminjanje velikosti vrstic");
        zoomHint.setFont(UiConfig.FONT_SMALL);
        zoomHint.setForeground(UiConfig.TEXT_MUTED);
        zoomHint.setBorder(new EmptyBorder(UiConfig.PAD_INNER_SMALL, UiConfig.PAD, 0, UiConfig.PAD));
        zoomHint.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(zoomHint);

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(headerPanel);
        north.add(Box.createVerticalStrut(UiConfig.PAD));
        north.add(topPanel);

        mainPanel.add(north, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildButtonBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, UiConfig.GAP, 0));
        p.setOpaque(false);

        btnRefresh = createButton("⟳ OSVEŽI", UiConfig.BG_BAR, UiConfig.TEXT_ACCENT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::refreshTable);

        btnAdd = createButton("+ DODAJ ZAPOSLENEGA", UiConfig.PRIMARY, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W_LARGE, UiConfig.BTN_H, this::onAdd);

        btnEdit = createButton("✎ UREDI", UiConfig.SUCCESS, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::onEdit);

        btnDelete = createButton("🗑 IZBRŠI", UiConfig.DANGER, UiConfig.PRIMARY_TEXT,
                UiConfig.BTN_W, UiConfig.BTN_H, this::onDelete);

        p.add(btnRefresh);
        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);

        return p;
    }

    private JButton createButton(String text, Color bg, Color fg, int w, int h, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        b.setFont(UiConfig.FONT_BASE_BOLD);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setMinimumSize(new Dimension(w, h));
        b.setPreferredSize(new Dimension(w, h));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });

        return b;
    }

    private JTable createEmployeeTable() {
        String[] columns = {"ID", "IME", "PRIIMEK", "DELOVNO MESTO", "ODDELEK", "PLAČA", "DATUM ZAPOSLITVE"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable t = new JTable(model);

        t.setRowHeight(UiConfig.TABLE_ROW_H);
        t.setFont(UiConfig.FONT_BASE_BOLD);
        t.setBackground(UiConfig.TABLE_BG);
        t.setForeground(UiConfig.TEXT);
        t.setGridColor(UiConfig.TABLE_GRID);
        t.setShowGrid(true);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setSelectionBackground(UiConfig.TABLE_ROW_SELECTED);
        t.setSelectionForeground(UiConfig.TEXT_BOLD);
        t.setRowMargin(2);

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(UiConfig.TABLE_HEADER_BG_DARK);
        header.setForeground(UiConfig.TABLE_HEADER_FG);
        header.setFont(UiConfig.FONT_H3);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, UiConfig.BORDER_WIDTH_THICK, 0, UiConfig.BORDER_ACCENT),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER)
        ));

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

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Ali ste prepričani, da se želite odjaviti?",
                "POTRDI ODJAVO",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            LoginWindow loginWindow = new LoginWindow(controller);
            loginWindow.setVisible(true);
        }
    }

    private void refreshTable() {
        try {
            if (employeeTable.getRowSorter() != null) {
                employeeTable.getRowSorter().setSortKeys(null);
            }

            controller.refreshApp();
            controller.loadEmployees((DefaultTableModel) employeeTable.getModel());

            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "NAPAKA", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "IZBERI ZAPOSLENEGA!");
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
            JOptionPane.showMessageDialog(this, "IZBERI ZAPOSLENEGA!");
            return;
        }

        int row = employeeTable.convertRowIndexToModel(viewRow);
        int employeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "IZBRŠEM IZBIRANEGA ZAPOSLENEGA?", "POTRDI BRISANJE", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteEmployee(employeeId);
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "NAPAKA", JOptionPane.ERROR_MESSAGE);
        }
    }
}