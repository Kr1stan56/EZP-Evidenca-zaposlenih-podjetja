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

    // UI references (da jih lahko applyUi() restyla po refreshu)
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel topPanel;
    private JPanel buttonBar;

    private JLabel lblDashboard;
    private JLabel titleLabel;

    private JScrollPane tableScroll;

    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;

    public MainWindow(AppController controller) {
        this.controller = controller;

        setTitle("EZP – Evidenca zaposlenih podjetja");

        // Če hočeš 100% DB: premakni w/h v DB ključe in beri v UiConfig
        setSize(1000, 600);

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

        // header (dashboard)
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(0, 4, 10, 0));

        lblDashboard = new JLabel("DASHBOARD");
        lblDashboard.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(lblDashboard);

        // top bar
        titleLabel = new JLabel("Seznam zaposlenih");
        topPanel.add(titleLabel, BorderLayout.WEST);

        buttonBar = buildButtonBar();
        topPanel.add(buttonBar, BorderLayout.EAST);

        // scrollpane
        tableScroll.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        // north wrapper
        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(headerPanel);
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

        // style (bo še enkrat nastavljen v applyUi)
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
            topPanel.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));
        }
        if (titleLabel != null) {
            titleLabel.setFont(UiConfig.FONT_H1);
            titleLabel.setForeground(UiConfig.TEXT);
        }

        restyleButton(btnRefresh, UiConfig.BG_BAR, UiConfig.TEXT_MUTED, UiConfig.BTN_H, UiConfig.BTN_H);
        restyleButton(btnAdd, UiConfig.PRIMARY, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);
        restyleButton(btnEdit, UiConfig.SUCCESS, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);
        restyleButton(btnDelete, UiConfig.DANGER, UiConfig.PRIMARY_TEXT, UiConfig.BTN_W, UiConfig.BTN_H);

        //
        if (tableScroll != null) {
            tableScroll.getViewport().setBackground(UiConfig.BG_CARD);
            tableScroll.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));
        }

        // table
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

    private void refreshTable() {
        try {
            if (employeeTable.getRowSorter() != null) {
                employeeTable.getRowSorter().setSortKeys(null);
            }

            // 1) reload UiConfig iz baze
            controller.refreshApp();

            // 2) apply novih nastavitev na komponente
            applyUi();

            // 3) reload data
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
