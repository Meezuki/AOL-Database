package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
// Import model karena helper ini akan mengembalikan objek Model
import model.Menu; 

public class DatabaseHelper {

    // 1. Validasi Login
    public static boolean validateLogin(String staffId, String password) {
        String query = "SELECT * FROM Kasir WHERE id_kasir = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            if (conn == null) return false;

            stmt.setString(1, staffId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // True jika user ditemukan
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Ambil Semua Menu
    public static List<Menu> getAllMenu() {
        List<Menu> menuList = new ArrayList<>();
        String query = "SELECT * FROM Menu";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Mapping dari Database Row ke Java Object (Model)
                Menu m = new Menu(
                    rs.getString("kode_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga_satuan"),
                    rs.getString("id_kategori")
                );
                menuList.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menuList;
    }
    
    public static boolean saveTransaction(String staffId, List<CartItem> items) {
        String insertHeader = "INSERT INTO Transaksi (no_nota, id_kasir, tanggal_transaksi, id_pelanggan, no_meja) VALUES (?, ?, ?, ?, ?)";
        String insertDetail = "INSERT INTO Detail_Pesanan (no_nota, kode_menu, jumlah, subtotal, harga_satuan_saat_itu) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;

        try {
            conn = DatabaseConnection.connect();
            if (conn == null) return false;

            // --- MULAI TRANSAKSI ---
            conn.setAutoCommit(false); // Matikan auto-save agar bisa rollback jika error

            // 1. Generate No Nota Unik (Format: TRX + Waktu System)
            // Contoh: TRX-1701234567
            String noNota = "TRX-" + System.currentTimeMillis();
            
            // 2. Simpan Header
            psHeader = conn.prepareStatement(insertHeader);
            psHeader.setString(1, noNota);
            psHeader.setString(2, staffId); // Ambil dari Session
            psHeader.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Waktu sekarang
            psHeader.setString(4, null); // Pelanggan kosong (Tamu)
            psHeader.setString(5, "00"); // Meja default (bisa diupdate nanti)
            
            psHeader.executeUpdate();

            // 3. Simpan Detail (Looping isi keranjang)
            psDetail = conn.prepareStatement(insertDetail);
            
            for (CartItem item : items) {
                psDetail.setString(1, noNota);
                psDetail.setString(2, item.getKodeMenu()); // Pastikan di CartItem ada getKodeMenu()
                psDetail.setInt(3, item.getQty());
                psDetail.setInt(4, item.getTotal());
                psDetail.setInt(5, item.getPrice()); // Harga Snapshot
                
                psDetail.addBatch(); // Tambahkan ke antrian batch
            }
            
            psDetail.executeBatch(); // Jalankan semua insert detail sekaligus

            // --- COMMIT (SIMPAN PERMANEN) ---
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Batalkan semua jika ada error
            } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            // Tutup semua resource manual agar aman
            try { if (psHeader != null) psHeader.close(); } catch (Exception e) {}
            try { if (psDetail != null) psDetail.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
    
    
}