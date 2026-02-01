package ui;

import controller.AppController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    private JTable employeeTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private final AppController controller;
    private final int rowHeight = 26;

    public MainWindow(AppController controller) {
        this.controller = controller;

        setTitle("EZP – Evidenca zaposlenih podjetja");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupWindow();
        refreshTable();

        setVisible(true);
    }

    private void setupWindow() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 244, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(235, 240, 248));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 235)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Seznam Zaposlenih");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(20, 60, 130));
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel buttonBar = createButtonBar();
        topBar.add(buttonBar, BorderLayout.EAST);

        employeeTable = createEmployeeTable();

        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 235)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createButtonBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);

        addButton = new JButton("➕ Dodaj zaposlenega");
        editButton = new JButton("✓ Uredi");
        deleteButton = new JButton("✖ Izbriši");
        refreshButton = new JButton("⟳");

        stylePrimary(addButton);
        styleSuccess(editButton);
        styleDanger(deleteButton);
        styleIcon(refreshButton);

        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        deleteButton.addActionListener(e -> onDelete());
        refreshButton.addActionListener(e -> refreshTable());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private JTable createEmployeeTable() {
        String[] columns = { "Ime", "Priimek", "Delovno mesto", "Oddelek", "Plača", "Datum zaposlitve" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(rowHeight);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(220, 228, 240));
        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setBackground(new Color(30, 95, 190));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        return table;
    }

    private void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
            controller.loadEmployees(model);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onAdd() {
        JOptionPane.showMessageDialog(this, "TODO: Dodaj zaposlenega (prek controllerja)");
        // controller.addEmployee(...)
        // refreshTable();
    }

    private void onEdit() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }
        JOptionPane.showMessageDialog(this, "TODO: Uredi zaposlenega (prek controllerja)");
        // controller.updateEmployee(...)
        // refreshTable();
    }

    private void onDelete() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Izbrišem?", "Potrdi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        JOptionPane.showMessageDialog(this, "TODO: Izbriši zaposlenega (prek controllerja)");
        // controller.deleteEmployee(...)
        // refreshTable();
    }


    private void stylePrimary(JButton b) {
        b.setBackground(new Color(30, 95, 190));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void styleSuccess(JButton b) {
        b.setBackground(new Color(46, 160, 67));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void styleDanger(JButton b) {
        b.setBackground(new Color(200, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void styleIcon(JButton b) {
        b.setBackground(new Color(235, 240, 248));
        b.setForeground(new Color(20, 60, 130));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(44, 28));
    }
}
