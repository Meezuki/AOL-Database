package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.CategoryModel;
import model.CustomerModel;
import model.Kasir; // Menggunakan model Kasir user
import model.Menu;
import model.TransactionModel; 

public class DatabaseHelper {

    // ==========================================
    // 1. AUTH & STAFF (Tabel: Kasir)
    // ==========================================

    public static boolean validateLogin(String staffId, String password) {
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

    // Mengambil List Kasir (Update: Return List<Kasir>)
    public static List<Kasir> getAllStaff() {
        List<Kasir> list = new ArrayList<>();
        String query = "SELECT * FROM Kasir"; 
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Kasir(
                    rs.getString("id_kasir"), 
                    rs.getString("nama_kasir"),
                    rs.getString("password")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insertStaff(String id, String nama, String pass) {
        String query = "INSERT INTO Kasir (id_kasir, nama_kasir, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id); ps.setString(2, nama); ps.setString(3, pass);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean updateStaff(String id, String nama, String pass) {
        String query = "UPDATE Kasir SET nama_kasir=?, password=? WHERE id_kasir=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama); ps.setString(2, pass); ps.setString(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteStaff(String id) {
        String query = "DELETE FROM Kasir WHERE id_kasir=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // ==========================================
    // 2. MENU & KATEGORI
    // ==========================================

    public static List<Menu> getAllMenu() {
        List<Menu> menuList = new ArrayList<>();
        String query = "SELECT * FROM Menu";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                menuList.add(new Menu(
                    rs.getString("kode_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga_satuan"),
                    rs.getString("id_kategori")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return menuList;
    }

    // Untuk Dropdown (String)
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT id_kategori, nama_kategori FROM Kategori";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("id_kategori") + " - " + rs.getString("nama_kategori"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return categories;
    }

    // Untuk Tabel Manage Kategori (Model)
    public static List<CategoryModel> getCategoryList() {
        List<CategoryModel> list = new ArrayList<>();
        String query = "SELECT * FROM Kategori";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while(rs.next()) {
                list.add(new CategoryModel(rs.getString("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insertMenu(String id, String nama, int harga, String idKategori) {
        String query = "INSERT INTO Menu (kode_menu, nama_menu, harga_satuan, id_kategori) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id); ps.setString(2, nama); ps.setInt(3, harga); ps.setString(4, idKategori);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean updateMenu(String id, String nama, int harga, String idKategori) {
        String query = "UPDATE Menu SET nama_menu = ?, harga_satuan = ?, id_kategori = ? WHERE kode_menu = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama); ps.setInt(2, harga); ps.setString(3, idKategori); ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteMenu(String id) {
        String query = "DELETE FROM Menu WHERE kode_menu = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public static boolean insertCategory(String id, String nama) {
        String query = "INSERT INTO Kategori (id_kategori, nama_kategori) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id); ps.setString(2, nama);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean updateCategory(String id, String nama) {
        String query = "UPDATE Kategori SET nama_kategori = ? WHERE id_kategori = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama); ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteCategory(String id) {
        String query = "DELETE FROM Kategori WHERE id_kategori = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // ==========================================
    // 3. PELANGGAN
    // ==========================================

    public static List<CustomerModel> getAllCustomers() {
        List<CustomerModel> list = new ArrayList<>();
        String query = "SELECT * FROM Pelanggan"; 
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new CustomerModel(
                    rs.getString("id_pelanggan"), 
                    rs.getString("nama_pelanggan"),
                    rs.getString("tipe_membership"),
                    rs.getString("no_telp_pelanggan")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insertCustomer(String id, String nama, String tipe, String telp) {
        String query = "INSERT INTO Pelanggan (id_pelanggan, nama_pelanggan, tipe_membership, no_telp_pelanggan) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id); ps.setString(2, nama); ps.setString(3, tipe); ps.setString(4, telp);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean updateCustomer(String id, String nama, String tipe, String telp) {
        String query = "UPDATE Pelanggan SET nama_pelanggan=?, tipe_membership=?, no_telp_pelanggan=? WHERE id_pelanggan=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama); ps.setString(2, tipe); ps.setString(3, telp); ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteCustomer(String id) {
        String query = "DELETE FROM Pelanggan WHERE id_pelanggan=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // ==========================================
    // 4. TRANSAKSI (Dengan No Meja)
    // ==========================================

    // Update: Parameter noMeja ditambahkan
    public static boolean saveTransaction(String staffId, String customerId, String noMeja, List<CartItem> items) {
        String insertHeader = "INSERT INTO Transaksi (no_nota, id_kasir, tanggal_transaksi, id_pelanggan, no_meja) VALUES (?, ?, ?, ?, ?)";
        String insertDetail = "INSERT INTO Detail_Pesanan (no_nota, kode_menu, jumlah, subtotal, harga_satuan_saat_itu) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;

        try {
            conn = DatabaseConnection.connect();
            if (conn == null) return false;

            conn.setAutoCommit(false); 

            // Generate No Nota (Max 13 digit untuk amankan VARCHAR)
            String noNota = "TRX-" + (System.currentTimeMillis() % 1000000000);
            
            // A. Insert Header
            psHeader = conn.prepareStatement(insertHeader);
            psHeader.setString(1, noNota);
            psHeader.setString(2, staffId);
            psHeader.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            psHeader.setString(4, customerId); // Bisa NULL
            
            // Logic No Meja: Kalau kosong, default "00"
            psHeader.setString(5, (noMeja == null || noMeja.isEmpty()) ? "00" : noMeja); 
            
            psHeader.executeUpdate();

            // B. Insert Detail
            psDetail = conn.prepareStatement(insertDetail);
            for (CartItem item : items) {
                psDetail.setString(1, noNota);
                psDetail.setString(2, item.getKodeMenu());
                psDetail.setInt(3, item.getQty());
                psDetail.setInt(4, item.getTotal());
                psDetail.setInt(5, item.getPrice());
                psDetail.addBatch();
            }
            psDetail.executeBatch(); 

            conn.commit(); 
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
    
    // Riwayat Transaksi
    public static List<TransactionModel> getTransactionHistory() {
        List<TransactionModel> history = new ArrayList<>();
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
                history.add(new TransactionModel(
                    rs.getString("no_nota"),
                    tgl,
                    rs.getString("nama_kasir"),
                    rs.getInt("grand_total")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return history;
    }
    
    
    
 // ==========================================
    // 5. REPORTING / LAPORAN
    // ==========================================

    // A. Laporan Meja Terlaris
    public static List<model.ReportModel> getHotTables() {
        List<model.ReportModel> list = new ArrayList<>();
        String query = "SELECT no_meja, COUNT(no_nota) as total FROM Transaksi GROUP BY no_meja ORDER BY total DESC";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Handle meja kosong (Take Away)
                String meja = rs.getString("no_meja");
                if (meja == null || meja.equals("00")) meja = "Take Away";
                
                list.add(new model.ReportModel(meja, rs.getInt("total")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // B. Laporan Kategori Terlaris (Berdasarkan Omzet)
    public static List<model.ReportModel> getBestCategory() {
        List<model.ReportModel> list = new ArrayList<>();
        String query = 
            "SELECT k.nama_kategori, SUM(d.subtotal) as omzet " +
            "FROM Detail_Pesanan d " +
            "JOIN Menu m ON d.kode_menu = m.kode_menu " +
            "JOIN Kategori k ON m.id_kategori = k.id_kategori " +
            "GROUP BY k.nama_kategori ORDER BY omzet DESC";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new model.ReportModel(rs.getString("nama_kategori"), rs.getInt("omzet")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // C. Laporan Menu Terlaris (Top 5 Qty)
    public static List<model.ReportModel> getTopMenu() {
        List<model.ReportModel> list = new ArrayList<>();
        String query = 
            "SELECT m.nama_menu, SUM(d.jumlah) as porsi " +
            "FROM Detail_Pesanan d " +
            "JOIN Menu m ON d.kode_menu = m.kode_menu " +
            "GROUP BY m.nama_menu ORDER BY porsi DESC LIMIT 5";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new model.ReportModel(rs.getString("nama_menu"), rs.getInt("porsi")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // D. Laporan Kinerja Staff
    public static List<model.ReportModel> getTopStaff() {
        List<model.ReportModel> list = new ArrayList<>();
        String query = 
            "SELECT k.nama_kasir, COUNT(t.no_nota) as transaksi " +
            "FROM Transaksi t JOIN Kasir k ON t.id_kasir = k.id_kasir " +
            "GROUP BY k.nama_kasir ORDER BY transaksi DESC";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new model.ReportModel(rs.getString("nama_kasir"), rs.getInt("transaksi")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    
    
    
    
}