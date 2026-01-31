package ui;

import db.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainWindow extends JFrame {

    private JTable employeeTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private Database db;

    public MainWindow(Database db) {
        this.db = db;

        setTitle("EZP – Evidencia zaposlenih podjetja");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupWindow();
        loadEmployeeData();
    }

    private void setupWindow() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Naslov
        JLabel titleLabel = new JLabel("Seznam Zaposlenih");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);

        // Gumbi
        JPanel buttonPanel = createButtonPanel();

        // Tabela
        employeeTable = createEmployeeTable();
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton = new JButton("Dodaj Zaposlenega");
        editButton = new JButton("Uredi");
        deleteButton = new JButton("Izbriši");
        refreshButton = new JButton("Osveži");

        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        refreshButton.addActionListener(e -> loadEmployeeData());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private JTable createEmployeeTable() {
        String[] columnNames = {
                "ID", "Ime", "Priimek", "Delovno Mesto", "Oddelek", "Plača", "Datum Zaposlitve"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        // Skrijemo ID stolpec
        table.removeColumn(table.getColumnModel().getColumn(0));

        return table;
    }

    private void loadEmployeeData() {
        try {
            Connection conn = db.getConnection();

            // String sql = "SELECT * FROM get_all_employees()";

            // TODO: Implementiraj SQL za pridobitev zaposlenih



            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Napaka: " + e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEmployee() {
        try {
            Connection conn = db.getConnection();

            // String sql = "SELECT add_employee(?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // TODO: Implementiraj SQL za dodajanje zaposlenega

            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Napaka: " + e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        // TODO: Implementiraj urejanje zaposlenega
        JOptionPane.showMessageDialog(this, "Funkcija za urejanje zaposlenega");
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Izbrišem?", "Potrdi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = db.getConnection();

                // String sql = "SELECT delete_employee(?)";

                // TODO: Implementiraj SQL za brisanje zaposlenega

                conn.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Napaka: " + e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}