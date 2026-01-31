
    public boolean register(String username, String password, String email) throws Exception {

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "SELECT register_user(?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                } else {
                    return false;
                }
            }
        }
    }