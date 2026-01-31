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
    private int RowHieght = 25;
    public MainWindow(Database db) throws Exception{
        this.db = db;

        setTitle("EZP – Evidencia zaposlenih podjetja");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        setupWindow();
        try {
            loadEmployeeData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupWindow() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Seznam Zaposlenih");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);

        JPanel buttonPanel = createButtonPanel();

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
        refreshButton.addActionListener(e -> {
            try {
                loadEmployeeData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });


        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private JTable createEmployeeTable() {
        String[] columns = {
                "Ime", "Priimek", "Telefon", "Plača", "Kraj"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(RowHieght);
        return table;
    }



    private void loadEmployeeData() throws Exception {
        Connection conn = db.getConnection();

        if (conn == null || conn.isClosed()) {
            throw new IllegalStateException("DB connection ni odprta");
        }

        String sql = "SELECT * FROM get_all_employees()";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            Object[] row = {
                    rs.getInt("id"),
                    rs.getString("ime"),
                    rs.getString("priimek"),
                    rs.getString("email"),
                    rs.getString("telefon"),
                    rs.getFloat("placa"),
                    rs.getDate("datum_zaposlitve"),
                    rs.getString("delovno_mesto"),
                    rs.getString("oddelek"),
                    rs.getString("kraj"),
                    rs.getString("izobrazba")
            };
            model.addRow(row);
        }

        rs.close();
        ps.close();
        //conn.close();
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