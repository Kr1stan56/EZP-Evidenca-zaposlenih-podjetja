package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import controller.AppController;

public class RegisterWindow extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JButton btnRegister;
    private JButton btnCancel;

    private boolean registrationSuccess = false;
    private AppController controller;

    public RegisterWindow(Frame owner, AppController controller) {
        super(owner, "Registracija novega administratorja", true);
        this.controller = controller;

        setSize(450, 500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(UiConfig.BG_APP);
        mainPanel.setBorder(new EmptyBorder(UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Registracija");
        lblTitle.setFont(UiConfig.FONT_H1);
        lblTitle.setForeground(UiConfig.TEXT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Ustvari nov administratorski račun");
        lblSubtitle.setFont(UiConfig.FONT_SMALL);
        lblSubtitle.setForeground(UiConfig.TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSubtitle);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        txtUsername = createTextField();
        txtPassword = createPasswordField();
        txtConfirmPassword = createPasswordField();
        txtEmail = createTextField();

        int row = 0;

        gbc.gridy = row++;
        formPanel.add(createLabel("Uporabniško ime:"), gbc);

        gbc.gridy = row++;
        formPanel.add(txtUsername, gbc);

        gbc.gridy = row++;
        formPanel.add(createLabel("Geslo:"), gbc);

        gbc.gridy = row++;
        formPanel.add(txtPassword, gbc);

        gbc.gridy = row++;
        formPanel.add(createLabel("Potrdi geslo:"), gbc);

        gbc.gridy = row++;
        formPanel.add(txtConfirmPassword, gbc);

        gbc.gridy = row++;
        formPanel.add(createLabel("E-pošta:"), gbc);

        gbc.gridy = row++;
        formPanel.add(txtEmail, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        btnRegister = new JButton("Registriraj");
        btnRegister.setFont(UiConfig.FONT_BASE);
        btnRegister.setBackground(UiConfig.PRIMARY);
        btnRegister.setForeground(UiConfig.PRIMARY_TEXT);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> onRegister());

        btnCancel = new JButton("Prekliči");
        btnCancel.setFont(UiConfig.FONT_BASE);
        btnCancel.setBackground(UiConfig.BORDER);
        btnCancel.setForeground(UiConfig.TEXT);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        gbc.gridy = row++;
        gbc.insets = new Insets(15, 10, 0, 10);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        getRootPane().setDefaultButton(btnRegister);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiConfig.FONT_SMALL);
        label.setForeground(UiConfig.TEXT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(UiConfig.FONT_BASE);
        field.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UiConfig.FONT_BASE);
        field.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private void onRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String email = txtEmail.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vsa polja so obvezna!", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Gesli se ne ujemata!", "Napaka", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Geslo mora vsebovati vsaj 6 znakov!", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Vnesite veljaven e-poštni naslov!", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // KLIČITE REGISTRACIJO PREK KONTROLERJA
            boolean success = controller.registerAdmin(username, password, email);

            if (success) {
                registrationSuccess = true;
                JOptionPane.showMessageDialog(this,
                        "Registracija uspešna!\n\nUporabniško ime: " + username + "\nE-pošta: " + email,
                        "Uspeh",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registracija ni uspela. Uporabniško ime ali e-pošta že obstaja.",
                        "Napaka",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Napaka pri registraciji: " + e.getMessage(),
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isRegistrationSuccess() {
        return registrationSuccess;
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }
}