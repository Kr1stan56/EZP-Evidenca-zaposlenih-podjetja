package ui;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginWindow() {

        setTitle("EZP – Prijava");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width / 3, screen.height / 3);
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    private void initUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiConfig.BG_APP);
        root.setBorder(BorderFactory.createEmptyBorder(
                UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD
        ));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiConfig.BG_BAR);
        header.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        JLabel lblTitle = new JLabel("Prijava v EZP");
        lblTitle.setFont(UiConfig.FONT_H1);
        lblTitle.setForeground(UiConfig.TEXT_MUTED);

        header.add(lblTitle, BorderLayout.WEST);


        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiConfig.BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtUsername = new JTextField();
        txtUsername.setFont(UiConfig.FONT_BASE);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UiConfig.FONT_BASE);

        btnLogin = new JButton("Prijava");
        btnLogin.setFont(UiConfig.FONT_BASE);
        btnLogin.setBackground(UiConfig.PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(
                new Dimension(UiConfig.BTN_W, UiConfig.BTN_H)
        );

        int r = 0;

        gbc.gridx = 0;
        gbc.gridy = r++;
        card.add(new JLabel("Uporabniško ime:"), gbc);

        gbc.gridy = r++;
        card.add(txtUsername, gbc);

        gbc.gridy = r++;
        card.add(new JLabel("Geslo:"), gbc);

        gbc.gridy = r++;
        card.add(txtPassword, gbc);

        gbc.gridy = r++;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(btnLogin, gbc);


        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        setContentPane(root);
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }
}
