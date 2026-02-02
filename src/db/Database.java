package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;

    private final String url =
            "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require";

    private final String user = "postgres.vuhabmtcbwlafsfwzyzu";
    private final String pass = "V6GFzDJ4Np2K2YkO";



    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(url, user, pass);

        System.out.println("[DB] Uspešno povezan na PostgreSQL");
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
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
