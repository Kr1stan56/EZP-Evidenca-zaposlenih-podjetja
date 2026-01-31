package ui;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnTempRegister; // ZAČASNO

    public LoginWindow() {
        setTitle("EZP – Prijava");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // velikost ~ 1/3 zaslona
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width / 3, screen.height / 3);
        setLocationRelativeTo(null); // center

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Prijava v EZP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Prijava");
        btnTempRegister = new JButton("⚠️ Temp register");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Uporabniško ime:"), gbc);

        gbc.gridy++;
        panel.add(txtUsername, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Geslo:"), gbc);

        gbc.gridy++;
        panel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(btnLogin, gbc);

        gbc.gridx = 1;
        panel.add(btnTempRegister, gbc);

        add(panel);
    }

    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnTempRegister() { return btnTempRegister; }
}
