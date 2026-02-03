    package ui;

    import controller.AppController;

    import javax.swing.*;
    import java.awt.*;
    import java.sql.Date;
    import java.sql.ResultSet;
    import java.sql.SQLException;

    public class AddEmployee extends JDialog {

        private JTextField txtIme, txtPriimek, txtEmail, txtTelefon, txtPlaca, txtDatum;
        private JComboBox<Item> cbDelovnoMesto, cbOddelek, cbKraj, cbIzobrazba;
        private JButton btnSave;

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

            setTitle(mode == EmployeeFormMode.CREATE ? "Dodaj zaposlenega" : "Uredi zaposlenega");
            setSize(520, 430);
            setLocationRelativeTo(owner);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            initUI();
            loadCombos();

            if (mode == EmployeeFormMode.EDIT) {
                loadEmployee();
            }
        }

        private void initUI() {
            setLayout(new GridLayout(0, 2, 8, 8));

            txtIme = new JTextField();
            txtPriimek = new JTextField();
            txtEmail = new JTextField();
            txtTelefon = new JTextField();
            txtPlaca = new JTextField();
            txtDatum = new JTextField();

            cbDelovnoMesto = new JComboBox<>();
            cbOddelek = new JComboBox<>();
            cbKraj = new JComboBox<>();
            cbIzobrazba = new JComboBox<>();

            btnSave = new JButton(mode == EmployeeFormMode.CREATE ? "Ustvari" : "Posodobi");

            add(new JLabel("Ime")); add(txtIme);
            add(new JLabel("Priimek")); add(txtPriimek);
            add(new JLabel("Email")); add(txtEmail);
            add(new JLabel("Telefon")); add(txtTelefon);
            add(new JLabel("Plača")); add(txtPlaca);
            add(new JLabel("Datum (YYYY-MM-DD)")); add(txtDatum);

            add(new JLabel("Delovno mesto")); add(cbDelovnoMesto);
            add(new JLabel("Oddelek")); add(cbOddelek);
            add(new JLabel("Kraj")); add(cbKraj);
            add(new JLabel("Izobrazba")); add(cbIzobrazba);

            add(new JLabel()); add(btnSave);

            btnSave.addActionListener(e -> onSave());
        }

        private void loadCombos() {
            try {
                try (ResultSet rs = controller.getDelovnaMesta()) {
                    while (rs.next()) {
                        cbDelovnoMesto.addItem(new Item(rs.getInt("id"), rs.getString("naziv")));
                    }
                }

                cbDelovnoMesto.addActionListener(e -> {
                    try {
                        refreshOddelkiForSelectedDelovnoMesto();
                    } catch (Exception ex) {
                        showError(ex);
                    }
                });

                refreshOddelkiForSelectedDelovnoMesto();

                try (ResultSet rs = controller.getKraji()) {
                    while (rs.next()) {
                        cbKraj.addItem(new Item(rs.getInt("id"), rs.getString("ime")));
                    }
                }

                try (ResultSet rs = controller.getIzobrazba()) {
                    while (rs.next()) {
                        cbIzobrazba.addItem(new Item(rs.getInt("id"), rs.getString("naziv")));
                    }
                }

            } catch (Exception e) {
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

                    // če tvoja get_employee_by_id vrača izobrazba_id, super:
                    int izobrazbaId = 0;
                    try {
                        izobrazbaId = rs.getInt("izobrazba_id");
                    } catch (SQLException ignored) {
                        // če stolpec ne obstaja v ResultSetu, pusti default
                    }

                    selectComboById(cbDelovnoMesto, delovnoMestoId);

                    // refresh oddelkov po izbiri delovnega mesta
                    refreshOddelkiForSelectedDelovnoMesto();

                    selectComboById(cbOddelek, oddelekId);
                    selectComboById(cbKraj, krajId);

                    if (izobrazbaId > 0) {
                        selectComboById(cbIzobrazba, izobrazbaId);
                    }
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

        private void refreshOddelkiForSelectedDelovnoMesto() throws Exception {
            Item dm = (Item) cbDelovnoMesto.getSelectedItem();
            if (dm == null) return;

            cbOddelek.removeAllItems();

            try (ResultSet rs = controller.getOddelki(dm.id)) {
                while (rs.next()) {
                    cbOddelek.addItem(new Item(rs.getInt("id"), rs.getString("naziv")));
                }
            }
        }

        private void onSave() {
            try {

                Item dm = (Item) cbDelovnoMesto.getSelectedItem();
                Item od = (Item) cbOddelek.getSelectedItem();
                Item kr = (Item) cbKraj.getSelectedItem();
                Item iz = (Item) cbIzobrazba.getSelectedItem();
                if (dm == null || od == null || kr == null || iz == null) {
                    JOptionPane.showMessageDialog(this, "Izberi delovno mesto, oddelek, kraj in izobrazbo.");
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
                            iz.id
                    );


                    JOptionPane.showMessageDialog(this, "Ustvarjen zaposleni. ID = " + newId);

                } else {

                    // Če tvoj AppController/update_employee še nima izobrazbe, moraš to dodati.
                    // Spodnji klic predpostavlja update z izobrazbo:
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
                            kr.id,
                            iz.id
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

                JOptionPane.showMessageDialog(this, msg, "Napaka", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, safe(e.getMessage()), "Napaka", JOptionPane.ERROR_MESSAGE);
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
            Item(int id, String text) { this.id = id; this.text = text; }
            public String toString() { return text; }
        }
    }
