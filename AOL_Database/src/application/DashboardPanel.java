package application;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.CartItem;
import model.Menu;
import util.DatabaseHelper;
import util.UserSession;

public class DashboardPanel {

    // Data Keranjang Belanja
    private ObservableList<CartItem> cartData = FXCollections.observableArrayList();
    private Label lblGrandTotal = new Label("Total: Rp 0");

    public BorderPane getView() {
        System.out.println("--- Memulai DashboardPanel ---"); // DEBUG

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- 1. HEADER (ATAS) ---
        HBox header = new HBox(20);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #2D3447;");
        
        Label title = new Label("AOL Kitchen POS");
        title.setTextFill(javafx.scene.paint.Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.getChildren().add(title);
        
        root.setTop(header); // Masukkan Header ke Layout


        // --- 2. MENU GRID (TENGAH) ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        FlowPane menuContainer = new FlowPane();
        menuContainer.setHgap(10);
        menuContainer.setVgap(10);
        menuContainer.setPadding(new Insets(10));

        // AMBIL DATA DARI DATABASE
        List<Menu> dbMenus = DatabaseHelper.getAllMenu();
        
        // DEBUG: Cek apakah data masuk?
        System.out.println("Jumlah Menu ditemukan: " + dbMenus.size());

        if (dbMenus.isEmpty()) {
            // Tampilkan label jika database kosong/gagal
            Label emptyLabel = new Label("Tidak ada menu ditemukan.\nCek koneksi database atau isi tabel Menu.");
            emptyLabel.setFont(Font.font("Arial", 16));
            menuContainer.getChildren().add(emptyLabel);
        } else {
            // Loop data menu
            for (Menu m : dbMenus) {
                System.out.println("Membuat tombol untuk: " + m.getNamaMenu()); // DEBUG
                
                Button btnMenu = new Button(m.toString());
                btnMenu.setPrefSize(120, 80);
                btnMenu.setWrapText(true);
                btnMenu.setStyle("-fx-background-color: #e0e0e0; -fx-cursor: hand;");
                
                btnMenu.setOnAction(e -> addToCart(m));
                
                menuContainer.getChildren().add(btnMenu);
            }
        }
        
        scrollPane.setContent(menuContainer);
        root.setCenter(scrollPane); // Masukkan Menu ke Layout


        // --- 3. KERANJANG (KANAN) ---
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(320);
        rightPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc;");

        Label cartTitle = new Label("Pesanan");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TableView<CartItem> table = new TableView<>();
        table.setItems(cartData);
        
        // Kolom Nama
        TableColumn<CartItem, String> colName = new TableColumn<>("Item");
        colName.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colName.setPrefWidth(120);

        // Kolom Qty
        TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colQty.setPrefWidth(50);
        
        // Kolom Total
        TableColumn<CartItem, Integer> colTotal = new TableColumn<>("Subtotal");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(90);

        table.getColumns().addAll(colName, colQty, colTotal);

        Button btnCheckout = new Button("BAYAR");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCheckout.setOnAction(e -> processCheckout());

        Button btnClear = new Button("Hapus");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> {
            cartData.clear();
            updateGrandTotal();
        });

        lblGrandTotal.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        rightPane.getChildren().addAll(cartTitle, table, lblGrandTotal, btnClear, btnCheckout);
        
        root.setRight(rightPane); // Masukkan Keranjang ke Layout

        return root;
    }

    // --- LOGIC METHODS ---

    private void addToCart(Menu menu) {
        for (CartItem item : cartData) {
            if (item.getNama().equals(menu.getNamaMenu())) {
                item.addQty(1);
                tableRefresh();
                updateGrandTotal();
                return;
            }
        }
        cartData.add(new CartItem(menu.getKodeMenu(), menu.getNamaMenu(), 1, menu.getHarga()));
        updateGrandTotal();
    }

    private void updateGrandTotal() {
        int total = 0;
        for (CartItem item : cartData) {
            total += item.getTotal();
        }
        lblGrandTotal.setText("Total: Rp " + total);
    }
    
    private void tableRefresh() {
        // Refresh hack untuk JavaFX TableView
        ObservableList<CartItem> tmp = FXCollections.observableArrayList(cartData);
        cartData.clear();
        cartData.addAll(tmp);
    }

    private void processCheckout() {
        if (cartData.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Keranjang masih kosong!");
            alert.showAndWait();
            return;
        }

        // Konfirmasi Pembayaran
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Total Pembayaran: " + lblGrandTotal.getText());
        confirm.setContentText("Apakah Anda yakin ingin memproses transaksi ini?");
        
        // Menunggu jawaban user (OK / Cancel)
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                
                // 1. Ambil ID Kasir dari Session
                String currentStaff = UserSession.getStaffId();
                if (currentStaff == null) currentStaff = "KS001"; // Fallback jika session hilang (debugging)

                // 2. Panggil DatabaseHelper untuk menyimpan
                // Kita perlu convert ObservableList ke ArrayList biasa
                boolean success = DatabaseHelper.saveTransaction(currentStaff, new java.util.ArrayList<>(cartData));

                if (success) {
                    Alert info = new Alert(AlertType.INFORMATION);
                    info.setTitle("Sukses");
                    info.setHeaderText(null);
                    info.setContentText("Transaksi Berhasil Disimpan!");
                    info.showAndWait();

                    // 3. Bersihkan Keranjang
                    cartData.clear();
                    updateGrandTotal();
                } else {
                    Alert error = new Alert(AlertType.ERROR);
                    error.setTitle("Gagal");
                    error.setContentText("Terjadi kesalahan saat menyimpan ke database.");
                    error.showAndWait();
                }
            }
        });
    }
}