package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import controller.AppController;

public class LoginWindow extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private AppController controller;

    public LoginWindow() {
        this(null);
    }

    public LoginWindow(AppController controller) {
        this.controller = controller;

        setTitle("EZP – Prijava");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.max(600, screen.width / 3), Math.max(600, screen.height / 3));
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, UiConfig.GAP_LARGE));
        root.setBackground(UiConfig.BG_APP);
        root.setBorder(new EmptyBorder(UiConfig.PAD_LARGE, UiConfig.PAD_LARGE, UiConfig.PAD_LARGE, UiConfig.PAD_LARGE));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, UiConfig.PAD, 0));

        JLabel lblTitle = new JLabel("PRIJAVA V EZP");
        lblTitle.setFont(UiConfig.FONT_H1_BOLD);
        lblTitle.setForeground(UiConfig.TEXT_BOLD);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Vpiši uporabniško ime in geslo");
        lblSub.setFont(UiConfig.FONT_BASE_BOLD);
        lblSub.setForeground(UiConfig.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(UiConfig.GAP_SMALL));
        header.add(lblSub);

        JPanel host = new JPanel(new GridBagLayout());
        host.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiConfig.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiConfig.BORDER_BOLD, UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_LARGE, UiConfig.PAD_LARGE, UiConfig.PAD_LARGE, UiConfig.PAD_LARGE)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UiConfig.GAP, 100, 0, 100);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        txtUsername = new JTextField();
        txtUsername.setFont(UiConfig.FONT_BASE);
        txtUsername.setForeground(UiConfig.TEXT);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiConfig.INPUT_BORDER, UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER)
        ));
        txtUsername.setBackground(UiConfig.INPUT_BG);
        txtUsername.setMinimumSize(new Dimension(0, UiConfig.INPUT_H));
        txtUsername.setPreferredSize(new Dimension(0, UiConfig.INPUT_H));

        txtPassword = new JPasswordField();
        txtPassword.setFont(UiConfig.FONT_BASE);
        txtPassword.setForeground(UiConfig.TEXT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiConfig.INPUT_BORDER, UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER, UiConfig.PAD_INNER)
        ));
        txtPassword.setBackground(UiConfig.INPUT_BG);
        txtPassword.setMinimumSize(new Dimension(0, UiConfig.INPUT_H));
        txtPassword.setPreferredSize(new Dimension(0, UiConfig.INPUT_H));

        btnLogin = new JButton("PRIJAVA");
        btnLogin.setFont(UiConfig.FONT_BASE_BOLD);
        btnLogin.setBackground(UiConfig.PRIMARY);
        btnLogin.setForeground(UiConfig.PRIMARY_TEXT);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiConfig.PRIMARY_DARK, UiConfig.BORDER_WIDTH),
                new EmptyBorder(UiConfig.PAD_INNER, UiConfig.PAD_INNER_LARGE, UiConfig.PAD_INNER, UiConfig.PAD_INNER_LARGE)
        ));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(true);
        btnLogin.setMinimumSize(new Dimension(UiConfig.BTN_W_LARGE, UiConfig.BTN_H_LARGE));
        btnLogin.setPreferredSize(new Dimension(UiConfig.BTN_W_LARGE, UiConfig.BTN_H_LARGE));

        btnRegister = new JButton("REGISTRACIJA");
        btnRegister.setFont(UiConfig.FONT_SMALL_BOLD);
        btnRegister.setBackground(UiConfig.BG_CARD);
        btnRegister.setForeground(UiConfig.PRIMARY);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiConfig.PRIMARY, UiConfig.BORDER_WIDTH_THIN),
                new EmptyBorder(UiConfig.PAD_INNER_SMALL, UiConfig.PAD_INNER, UiConfig.PAD_INNER_SMALL, UiConfig.PAD_INNER)
        ));
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(true);
        btnRegister.setMinimumSize(new Dimension(UiConfig.BTN_W, UiConfig.BTN_H));

        getRootPane().setDefaultButton(btnLogin);

        int r = 0;

        gbc.gridy = r++;
        card.add(labelBold("UPORABNIŠKO IME"), gbc);

        gbc.gridy = r++;
        card.add(txtUsername, gbc);

        gbc.gridy = r++;
        gbc.insets = new Insets(UiConfig.GAP_LARGE, 100, 0, 100);
        card.add(labelBold("GESLO"), gbc);

        gbc.gridy = r++;
        card.add(txtPassword, gbc);

        gbc.gridy = r++;
        gbc.insets = new Insets(UiConfig.PAD, 0, UiConfig.GAP, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        card.add(btnLogin, gbc);

        gbc.gridy = r++;
        gbc.insets = new Insets(UiConfig.GAP, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnRegister, gbc);

        host.add(card);

        root.add(header, BorderLayout.NORTH);
        root.add(host, BorderLayout.CENTER);

        setContentPane(root);

        btnRegister.addActionListener(e -> onRegister());
        btnLogin.addActionListener(e -> checkLogin());
    }

    private JLabel labelBold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UiConfig.FONT_SMALL_BOLD);
        l.setForeground(UiConfig.TEXT_BOLD);
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

    private void checkLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Izpolni vsa polja!");
            return;
        }

        try {
            String[] userData = controller.getAuthService().login(username, password);

            if (userData != null) {
                String userEmail = userData[1];

                dispose();
                controller.refreshApp();
                new MainWindow(controller, userEmail, username);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Napačno uporabniško ime ali geslo.\n\n" +
                                "Prosimo obrnite se na support:\n" +
                                "blaz.kristan.bk@gmail.com",
                        "Napaka pri prijavi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Napaka: " + e.getMessage());
        }
    }

    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnRegister() { return btnRegister; }
}