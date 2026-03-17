package ui;

import controller.AppController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddEmployee extends JDialog {

    private JTextField txtIme, txtPriimek, txtEmail, txtTelefon, txtPlaca, txtDatum;
    private JComboBox<Item> cbDelovnoMesto, cbOddelek, cbKraj;
    private JButton btnSave;
    private JButton btnCancel;

    private final AppController controller;
    private final EmployeeFormMode mode;
    private final int employeeId;

    public AddEmployee(Frame owner, AppController controller) {
        this(owner, controller, EmployeeFormMode.CREATE, -1);
    }

    public AddEmployee(Frame owner, AppController controller, int employeeId) {
        this(owner, controller, EmployeeFormMode.EDIT, employeeId);
    }

    private AddEmployee(Frame owner, AppController controller,
                        EmployeeFormMode mode, int employeeId) {

        super(owner, true);
        this.controller = controller;
        this.mode = mode;
        this.employeeId = employeeId;

        setTitle(mode == EmployeeFormMode.CREATE ? "DODAJ ZAPOSLENEGA" : "UREDI ZAPOSLENEGA");
        setSize(600, 550);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadCombos();

        if (mode == EmployeeFormMode.EDIT) {
            loadEmployee();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(mode == EmployeeFormMode.CREATE ? "DODAJ ZAPOSLENEGA" : "UREDI ZAPOSLENEGA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtIme = createTextField();
        txtPriimek = createTextField();
        txtEmail = createTextField();
        txtTelefon = createTextField();
        txtPlaca = createTextField();
        txtDatum = createTextField();

        cbDelovnoMesto = createComboBox();
        cbOddelek = createComboBox();
        cbKraj = createComboBox();

        formPanel.add(createLabel("IME:"));
        formPanel.add(txtIme);
        formPanel.add(createLabel("PRIIMEK:"));
        formPanel.add(txtPriimek);
        formPanel.add(createLabel("EMAIL:"));
        formPanel.add(txtEmail);
        formPanel.add(createLabel("TELEFON:"));
        formPanel.add(txtTelefon);
        formPanel.add(createLabel("PLAČA:"));
        formPanel.add(txtPlaca);
        formPanel.add(createLabel("DATUM (YYYY-MM-DD):"));
        formPanel.add(txtDatum);
        formPanel.add(createLabel("DELOVNO MESTO:"));
        formPanel.add(cbDelovnoMesto);
        formPanel.add(createLabel("ODDELEK:"));
        formPanel.add(cbOddelek);
        formPanel.add(createLabel("KRAJ:"));
        formPanel.add(cbKraj);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnSave = createButton(mode == EmployeeFormMode.CREATE ? "USTVARI" : "POSODOBI",
                new Color(46, 204, 113), Color.WHITE, 120, 40);
        btnCancel = createButton("PREKLIČI", new Color(231, 76, 60), Color.WHITE, 120, 40);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(0, 0, 0, 0)
        ));
        centerPanel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        getRootPane().setDefaultButton(btnSave);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);

        // ENOSTAVNA MEJA BREZ NOTRANJIH MEJ
        field.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));

        field.setMinimumSize(new Dimension(200, 35));
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }
    public void refreshUI() {
        SwingUtilities.invokeLater(() -> {
            Container contentPane = getContentPane();
            removeAll();
            initUI();
            loadCombos(); // Ponovno naloži podatke iz combo boxov
            revalidate();
            repaint();
        });
    }
    private JComboBox<Item> createComboBox() {
        JComboBox<Item> combo = new JComboBox<>();
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);

        // ENOSTAVNA MEJA BREZ NOTRANJIH MEJ
        combo.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));

        combo.setMinimumSize(new Dimension(200, 35));
        combo.setPreferredSize(new Dimension(200, 35));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setForeground(Color.BLACK);
                    label.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
                    label.setOpaque(true);
                    label.setFont(new Font("Arial", Font.PLAIN, 14));
                    // ENOSTAVNA MEJA ZA ELEMENTE
                    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                return c;
            }
        });

        return combo;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JButton createButton(String text, Color bgColor, Color fgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);

        // ENOSTAVNA MEJA ZA GUMBE
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));

        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setMinimumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));

        return button;
    }

    private void loadCombos() {
        ResultSet rs = null;
        try {
            cbDelovnoMesto.removeAllItems();
            cbOddelek.removeAllItems();
            cbKraj.removeAllItems();

            rs = controller.getDelovnaMesta();
            while (rs.next()) {
                cbDelovnoMesto.addItem(new Item(rs.getInt("id"), rs.getString("naziv")));
            }
            closeQuietly(rs);

            cbDelovnoMesto.addActionListener(e -> {
                Item selected = (Item) cbDelovnoMesto.getSelectedItem();
                if (selected != null) {
                    ResultSet rs2 = null;
                    try {
                        cbOddelek.removeAllItems();
                        rs2 = controller.getOddelki(selected.id);
                        while (rs2.next()) {
                            cbOddelek.addItem(new Item(rs2.getInt("id"), rs2.getString("naziv")));
                        }
                    } catch (Exception ex) {
                        showError(ex);
                    } finally {
                        closeQuietly(rs2);
                    }
                }
            });

            rs = controller.getKraji();
            while (rs.next()) {
                cbKraj.addItem(new Item(rs.getInt("id"), rs.getString("ime")));
            }
            closeQuietly(rs);

            if (cbDelovnoMesto.getItemCount() > 0) {
                cbDelovnoMesto.setSelectedIndex(0);
            }

        } catch (Exception e) {
            closeQuietly(rs);
            showError(e);
        }
    }

    private void loadEmployee() {
        ResultSet rs = null;
        try {
            rs = controller.getEmployeeById(employeeId);

            if (rs.next()) {
                txtIme.setText(rs.getString("ime"));
                txtPriimek.setText(rs.getString("priimek"));
                txtEmail.setText(rs.getString("email"));
                txtTelefon.setText(rs.getString("telefon"));
                txtPlaca.setText(rs.getString("placa"));
                txtDatum.setText(rs.getDate("datum_zaposlitve").toString());

                int delovnoMestoId = rs.getInt("delovno_mesto_id");
                int oddelekId = rs.getInt("oddelek_id");
                int krajId = rs.getInt("kraj_id");

                selectComboById(cbDelovnoMesto, delovnoMestoId);
                selectComboById(cbOddelek, oddelekId);
                selectComboById(cbKraj, krajId);
            }

            closeQuietly(rs);

        } catch (Exception e) {
            closeQuietly(rs);
            showError(e);
        }
    }

    private void selectComboById(JComboBox<Item> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Item it = combo.getItemAt(i);
            if (it != null && it.id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void onSave() {
        try {
            Item dm = (Item) cbDelovnoMesto.getSelectedItem();
            Item od = (Item) cbOddelek.getSelectedItem();
            Item kr = (Item) cbKraj.getSelectedItem();

            if (dm == null || od == null || kr == null) {
                JOptionPane.showMessageDialog(this, "Izberi delovno mesto, oddelek in kraj.");
                return;
            }

            String ime = txtIme.getText().trim();
            String priimek = txtPriimek.getText().trim();

            if (ime.isEmpty() || priimek.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ime in priimek sta obvezna.");
                return;
            }

            float placa = Float.parseFloat(txtPlaca.getText().trim());
            Date datum = Date.valueOf(txtDatum.getText().trim());

            if (mode == EmployeeFormMode.CREATE) {
                int newId = controller.addEmployee(
                        ime,
                        priimek,
                        txtEmail.getText().trim(),
                        txtTelefon.getText().trim(),
                        placa,
                        datum,
                        dm.id,
                        od.id,
                        kr.id,
                        1
                );

                JOptionPane.showMessageDialog(this, "Ustvarjen zaposleni. ID = " + newId);

            } else {
                controller.updateEmployee(
                        employeeId,
                        ime,
                        priimek,
                        txtEmail.getText().trim(),
                        txtTelefon.getText().trim(),
                        placa,
                        datum,
                        dm.id,
                        od.id,
                        kr.id
                );

                JOptionPane.showMessageDialog(this, "Posodobljeno.");
            }

            dispose();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        if (e instanceof SQLException se) {
            String msg = "SQL napaka:\n"
                    + "Message: " + safe(se.getMessage()) + "\n"
                    + "SQLState: " + safe(se.getSQLState()) + "\n"
                    + "ErrorCode: " + se.getErrorCode();

            SQLException next = se.getNextException();
            if (next != null) {
                msg += "\n\nNextException: " + safe(next.getMessage());
            }

            JOptionPane.showMessageDialog(this, msg, "NAPAKA", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, safe(e.getMessage()), "NAPAKA", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "(brez sporočila)" : s;
    }

    private void closeQuietly(ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
    }

    static class Item {
        int id;
        String text;
        Item(int id, String text) {
            this.id = id;
            this.text = text != null ? text : "";
        }
        public String toString() {
            return text != null ? text : "";
        }
    }
}