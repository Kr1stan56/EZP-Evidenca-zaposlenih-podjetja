package service;

import db.Database;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class AuthService {
    private final Database db;

    public AuthService(Database db) {
        this.db = db;
    }

    // SPREMENJENO: login metoda naj vrne tudi email
    public String[] login(String username, String password) throws Exception {
        String sql = "SELECT password, email FROM login_admin(?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String hash = rs.getString("password");
                String email = rs.getString("email");

                if (BCrypt.checkpw(password, hash)) {
                    return new String[]{username, email}; //
                } else {
                    return null;
                }
            }
        }
    }

    public boolean register(String username, String password, String email) throws Exception {
        String sql = "SELECT register_admin(?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
                return false;
            }
        }
    }
}