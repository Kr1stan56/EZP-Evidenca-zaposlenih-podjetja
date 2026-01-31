package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;

    private final String url  = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String pass = "root";

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(url, user, pass);

        System.out.println("[DB] Uspešno povezan na PostgreSQL");
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            // če je zaprta ali null, jo avtomatsko ponovno odpri
            try {
                connect();
            } catch (SQLException | ClassNotFoundException e) {
                throw e;
            }
        }
        return conn;
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {}
    }
}
