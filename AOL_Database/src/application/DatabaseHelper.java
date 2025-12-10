package application; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {

    // Ganti dengan nama database, user, dan password MySQL Anda
    private static final String URL = "jdbc:mysql://localhost:3306/aol_kitchen";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    // Method untuk mendapatkan koneksi
    public static Connection connect() {
        try {
            // Load driver (opsional di Java baru, tapi bagus untuk memastikan)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
            return null;
        }
    }

    // Method khusus untuk validasi login
    public static boolean validateLogin(String staffId, String password) {
        // Query cek ID dan Password
        String query = "SELECT * FROM Kasir WHERE id_kasir = ? AND password = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            if (conn == null) return false;

            stmt.setString(1, staffId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            
            // Jika rs.next() bernilai true, berarti data ditemukan (Login Sukses)
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}