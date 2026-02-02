package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;

    private final String url =
            "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require";

    private final String user = "postgres.vuhabmtcbwlafsfwzyzu";
    private final String pass = "ej3EFzR8BD3cI0Bf"; // po testu RESET

    public void connect() {
        try {
            System.out.println("[DB] Connecting...");
            System.out.println("[DB] URL  = " + url);
            System.out.println("[DB] USER = " + user);

            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, pass);

            System.out.println("[DB] CONNECTED OK");
        } catch (SQLException e) {
            System.err.println("[DB] SQL ERROR");
            System.err.println("SQLState  = " + e.getSQLState());
            System.err.println("ErrorCode = " + e.getErrorCode());
            System.err.println("Message   = " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] PostgreSQL DRIVER NOT FOUND");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("[DB] CONNECTION CHECK FAILED");
            e.printStackTrace();
            connect();
        }
        return conn;
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("[DB] DISCONNECTED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
