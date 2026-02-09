package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import controller.AppController;

public class LoginWindow extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private AppController controller;

    // DODAJTE KONSTRUKTOR BREZ PARAMETRA
    public LoginWindow() {
        this(null);
    }

    public LoginWindow(AppController controller) {
        this.controller = controller;

        setTitle("EZP – Prijava");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.max(520, screen.width / 3), Math.max(400, screen.height / 3));
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UiConfig.BG_APP);
        root.setBorder(new EmptyBorder(UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 0, true),
                new EmptyBorder(0, 88, 0, 0)
        ));
        JLabel lblTitle = new JLabel("Prijava v EZP");
        lblTitle.setFont(UiConfig.FONT_H1);
        lblTitle.setForeground(UiConfig.TEXT);

        JLabel lblSub = new JLabel("Vpiši uporabniško ime in geslo");
        lblSub.setFont(UiConfig.FONT_SMALL);
        lblSub.setForeground(UiConfig.TEXT_MUTED);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        JPanel host = new JPanel(new GridBagLayout());
        host.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiConfig.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true), // rounded
                new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 150, 0, 150);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        txtUsername = new JTextField();
        txtUsername.setFont(UiConfig.FONT_BASE);
        txtUsername.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true),
                new EmptyBorder(8, 5, 8, 0)
        ));

        txtPassword = new JPasswordField();
        txtPassword.setFont(UiConfig.FONT_BASE);
        txtPassword.setBorder(new CompoundBorder(
                new LineBorder(UiConfig.BORDER, 1, true),
                new EmptyBorder(8, 5, 8, 0)
        ));

        btnLogin = new JButton("Prijava");
        btnLogin.setFont(UiConfig.FONT_BASE);
        btnLogin.setBackground(UiConfig.PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);

        btnRegister = new JButton("Registracija");
        btnRegister.setFont(UiConfig.FONT_SMALL);
        btnRegister.setBackground(UiConfig.BG_CARD);
        btnRegister.setForeground(UiConfig.PRIMARY);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(new EmptyBorder(4, 8, 4, 8));
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(false);

        getRootPane().setDefaultButton(btnLogin);

        int r = 0;

        gbc.gridy = r++;
        card.add(labelMuted("Uporabniško ime"), gbc);

        gbc.gridy = r++;
        card.add(txtUsername, gbc);

        gbc.gridy = r++;
        card.add(labelMuted("Geslo"), gbc);

        gbc.gridy = r++;
        card.add(txtPassword, gbc);

        gbc.gridy = r++;
        gbc.insets = new Insets(14, 0, 8, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        card.add(btnLogin, gbc);

        gbc.gridy = r++;
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnRegister, gbc);

        host.add(card);

        root.add(header, BorderLayout.NORTH);
        root.add(host, BorderLayout.CENTER);

        setContentPane(root);

        btnRegister.addActionListener(e -> onRegister());
    }

    private JLabel labelMuted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UiConfig.FONT_SMALL);
        l.setForeground(UiConfig.TEXT_MUTED);
        return l;
    }

    private void onRegister() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Napaka: Controller ni nastavljen!",
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        RegisterWindow registerDialog = new RegisterWindow(this, controller);
        registerDialog.setVisible(true);

        if (registerDialog.isRegistrationSuccess()) {
            txtUsername.setText(registerDialog.getUsername());
            txtPassword.setText(registerDialog.getPassword());

            JOptionPane.showMessageDialog(this,
                    "Registracija uspešna!\nSedaj se lahko prijavite z vašimi podatki.",
                    "Uspeh",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnRegister() { return btnRegister; }
}