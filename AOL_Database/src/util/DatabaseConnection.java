package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // Sesuaikan config database Anda
    private static final String URL = "jdbc:mysql://localhost:3306/aol_kitchen";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("Koneksi Error: " + e.getMessage());
            return null;
        }
    }
}