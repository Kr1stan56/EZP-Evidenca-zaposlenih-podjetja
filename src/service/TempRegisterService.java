package service;

import db.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TempRegisterService {

    private final Database db;

    public TempRegisterService(Database db) {
        this.db = db;
    }

    // ⚠️ IZBRIŠI PRED ODDAJO
    public void register(String username, String password) throws Exception {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "SELECT temp_create_admin(?, ?)";
        Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, hash);
        ps.execute();
    }
}
