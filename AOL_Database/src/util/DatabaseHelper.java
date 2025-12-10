package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Menu;
import model.TransactionModel; 

public class DatabaseHelper {

    // --- 1. VALIDASI LOGIN (Tabel: Kasir) ---
    public static boolean validateLogin(String staffId, String password) {
        // Sesuai ERD: id_kasir, password
        String query = "SELECT * FROM Kasir WHERE id_kasir = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            if (conn == null) return false;

            stmt.setString(1, staffId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 2. AMBIL SEMUA MENU (Tabel: Menu) ---
    public static List<Menu> getAllMenu() {
        List<Menu> menuList = new ArrayList<>();
        String query = "SELECT * FROM Menu";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Menu m = new Menu(
                    rs.getString("kode_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga_satuan"),
                    rs.getString("id_kategori") // Ambil kategori dari DB
                );
                menuList.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menuList;
    }
    
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        // Format String: "ID - Nama Kategori" (Contoh: "KAT01 - Makanan Berat")
        String query = "SELECT id_kategori, nama_kategori FROM Kategori";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String item = rs.getString("id_kategori") + " - " + rs.getString("nama_kategori");
                categories.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    
    // --- 3. SIMPAN TRANSAKSI (Tabel: Transaksi & Detail_Pesanan) ---
    public static boolean saveTransaction(String staffId, List<CartItem> items) {
        // Sesuai ERD: no_nota, id_kasir, tanggal_transaksi, id_pelanggan, no_meja
        String insertHeader = "INSERT INTO Transaksi (no_nota, id_kasir, tanggal_transaksi, id_pelanggan, no_meja) VALUES (?, ?, ?, ?, ?)";
        
        // Sesuai ERD: no_nota, kode_menu, jumlah, subtotal, harga_satuan_saat_itu
        String insertDetail = "INSERT INTO Detail_Pesanan (no_nota, kode_menu, jumlah, subtotal, harga_satuan_saat_itu) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;

        try {
            conn = DatabaseConnection.connect();
            if (conn == null) return false;

            conn.setAutoCommit(false); // Mulai Transaksi Database

            // A. Generate No Nota (Contoh: TRX-Waktu)
            String noNota = "TRX-" + System.currentTimeMillis();
            
            // B. Insert Header
            psHeader = conn.prepareStatement(insertHeader);
            psHeader.setString(1, noNota);
            psHeader.setString(2, staffId);
            psHeader.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            psHeader.setString(4, null); // id_pelanggan (Dibuat NULL/Guest sesuai ERD relasi 0..*)
            psHeader.setString(5, "00"); // no_meja (Default)
            
            psHeader.executeUpdate();

            // C. Insert Detail
            psDetail = conn.prepareStatement(insertDetail);
            
            for (CartItem item : items) {
                psDetail.setString(1, noNota);
                psDetail.setString(2, item.getKodeMenu()); // Pastikan CartItem return kode_menu
                psDetail.setInt(3, item.getQty());
                psDetail.setInt(4, item.getTotal()); // subtotal
                psDetail.setInt(5, item.getPrice()); // harga_satuan_saat_itu
                
                psDetail.addBatch();
            }
            
            psDetail.executeBatch(); 

            conn.commit(); // Simpan Permanen
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if (psHeader != null) psHeader.close(); } catch (Exception e) {}
            try { if (psDetail != null) psDetail.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
    
    // --- 4. RIWAYAT TRANSAKSI ---
    public static List<TransactionModel> getTransactionHistory() {
        List<TransactionModel> history = new ArrayList<>();
        
        // Join tabel sesuai ERD: Transaksi -> Kasir -> Detail_Pesanan
        String query = 
            "SELECT t.no_nota, t.tanggal_transaksi, k.nama_kasir, SUM(d.subtotal) as grand_total " +
            "FROM Transaksi t " +
            "JOIN Kasir k ON t.id_kasir = k.id_kasir " +
            "JOIN Detail_Pesanan d ON t.no_nota = d.no_nota " +
            "GROUP BY t.no_nota, t.tanggal_transaksi, k.nama_kasir " +
            "ORDER BY t.tanggal_transaksi DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String tgl = rs.getTimestamp("tanggal_transaksi").toString();
                
                TransactionModel tm = new TransactionModel(
                    rs.getString("no_nota"),
                    tgl,
                    rs.getString("nama_kasir"),
                    rs.getInt("grand_total")
                );
                history.add(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }
    
    // --- 5. INSERT MENU (Tabel: Menu) ---
    public static boolean insertMenu(String id, String nama, int harga, String idKategori) {
        String query = "INSERT INTO Menu (kode_menu, nama_menu, harga_satuan, id_kategori) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setInt(3, harga);
            ps.setString(4, idKategori); // Tidak lagi default value
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 6. UPDATE MENU ---
    public static boolean updateMenu(String id, String nama, int harga, String idKategori) {
        String query = "UPDATE Menu SET nama_menu = ?, harga_satuan = ?, id_kategori = ? WHERE kode_menu = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, nama);
            ps.setInt(2, harga);
            ps.setString(3, idKategori);
            ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 7. DELETE MENU ---
    public static boolean deleteMenu(String id) {
        String query = "DELETE FROM Menu WHERE kode_menu = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Gagal hapus: Menu mungkin sudah ada di riwayat transaksi.");
            return false;
        }
    }
}