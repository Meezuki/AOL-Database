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
    public static boolean saveTransaction(String staffId, String customerId, List<CartItem> items) {
        String insertHeader = "INSERT INTO Transaksi (no_nota, id_kasir, tanggal_transaksi, id_pelanggan, no_meja) VALUES (?, ?, ?, ?, ?)";
        String insertDetail = "INSERT INTO Detail_Pesanan (no_nota, kode_menu, jumlah, subtotal, harga_satuan_saat_itu) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;

        try {
            conn = DatabaseConnection.connect();
            if (conn == null) return false;

            conn.setAutoCommit(false); 

            // Generate No Nota (Aman < 20 karakter)
            String noNota = "TRX-" + (System.currentTimeMillis() % 1000000000);
            
            // Simpan Header
            psHeader = conn.prepareStatement(insertHeader);
            psHeader.setString(1, noNota);
            psHeader.setString(2, staffId);
            psHeader.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            // --- PERUBAHAN DI SINI ---
            // Jika customerId null, database akan menyimpannya sebagai NULL (Guest)
            psHeader.setString(4, customerId); 
            
            psHeader.setString(5, "00"); 
            psHeader.executeUpdate();

            // Simpan Detail (Sama seperti sebelumnya)
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
    
    public static boolean insertCategory(String id, String nama) {
        String query = "INSERT INTO Kategori (id_kategori, nama_kategori) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, nama);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 9. UPDATE KATEGORI ---
    public static boolean updateCategory(String id, String nama) {
        String query = "UPDATE Kategori SET nama_kategori = ? WHERE id_kategori = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 10. DELETE KATEGORI ---
    public static boolean deleteCategory(String id) {
        String query = "DELETE FROM Kategori WHERE id_kategori = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Gagal jika kategori masih dipakai oleh Menu
            System.out.println("Gagal hapus: Kategori sedang digunakan oleh Menu.");
            return false;
        }
    }
    
    // Helper tambahan untuk mengambil List object (opsional, untuk TableView Kategori)
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
    
    
 // --- UPDATE: AMBIL SEMUA PELANGGAN (LENGKAP) ---
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- BARU: INSERT PELANGGAN ---
    public static boolean insertCustomer(String id, String nama, String tipe, String telp) {
        String query = "INSERT INTO Pelanggan (id_pelanggan, nama_pelanggan, tipe_membership, no_telp_pelanggan) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, tipe);
            ps.setString(4, telp); // Boleh kosong jika user tidak input
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- BARU: UPDATE PELANGGAN ---
    public static boolean updateCustomer(String id, String nama, String tipe, String telp) {
        String query = "UPDATE Pelanggan SET nama_pelanggan=?, tipe_membership=?, no_telp_pelanggan=? WHERE id_pelanggan=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, tipe);
            ps.setString(3, telp);
            ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- BARU: DELETE PELANGGAN ---
    public static boolean deleteCustomer(String id) {
        String query = "DELETE FROM Pelanggan WHERE id_pelanggan=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false; // Gagal jika pelanggan sudah pernah transaksi (Foreign Key restrict)
        }
    }
    
    
    
}

