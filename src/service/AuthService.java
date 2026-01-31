package service;

import db.Database;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    private final Database db;

    public AuthService(Database db) {
        this.db = db;
    }

    public boolean login(String username, String password) throws Exception {

        String sql = "SELECT password FROM login_admin(?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return false;

                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            }
        }
    }






}