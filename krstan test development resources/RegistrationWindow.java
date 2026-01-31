package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationWindow extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    private JButton btnRegister;
    private JButton btnCancel;

    public RegistrationWindow() {
        setTitle("Registracija");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Registracija novega uporabnika");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Uporabniško ime:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        mainPanel.add(txtUsername, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Geslo:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        mainPanel.add(txtPassword, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        mainPanel.add(txtEmail, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnRegister = new JButton("Registriraj");
        btnCancel = new JButton("Prekliči");

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        setupActions();
    }

    private void setupActions() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void handleRegistration() {

    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }
}