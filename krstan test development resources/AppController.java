import ui.RegistrationWindow;



    private RegistrationWindow registrationWindow;





    private void showRegister() {

        registrationWindow = new RegistrationWindow();

        registrationWindow.getBtnRegister()
                .addActionListener(e -> handleRegister());

        registrationWindow.setVisible(true);
    }


v login


                if (registrationWindow != null)
                    registrationWindow.dispose();


show login

        loginWindow.getBtnRegister()
                .addActionListener(e -> showRegister());


    private void handleRegister() {

        try {
            String username = registrationWindow.getTxtUsername().getText().trim();
            String password = new String(registrationWindow.getTxtPassword().getPassword());
            String email = registrationWindow.getTxtEmail().getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(registrationWindow, "Izpolni vsa polja");
                return;
            }

            boolean success = authService.register(username, password, email);

            if (success) {

                JOptionPane.showMessageDialog(registrationWindow, "Registracija uspešna");
                registrationWindow.dispose();

            } else {
                JOptionPane.showMessageDialog(registrationWindow, "Uporabnik že obstaja");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(registrationWindow, "Napaka: " + e.getMessage());
            e.printStackTrace();
        }
    }