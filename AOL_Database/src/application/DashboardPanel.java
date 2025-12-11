package application;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox; // Import ComboBox
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.CartItem;
import model.CustomerModel; // Import CustomerModel
import model.Menu;
import util.DatabaseHelper;
import util.UserSession;

public class DashboardPanel {

    private ObservableList<CartItem> cartData = FXCollections.observableArrayList();
    private Label lblGrandTotal = new Label("Total: Rp 0");
    private TableView<CartItem> table; 
    
    // Tambahan: ComboBox Pelanggan
    private ComboBox<CustomerModel> cmbCustomer;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- 1. HEADER (ATAS) ---
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 5;");
        
        Label title = new Label("AOL Kitchen POS System");
        title.setTextFill(javafx.scene.paint.Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        
        // Menampilkan nama staff yang sedang login
        Label staffLabel = new Label("Staff: " + (UserSession.getStaffId() != null ? UserSession.getStaffId() : "Guest"));
        staffLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        
        // Spacer agar judul di kiri, nama staff di kanan
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, staffLabel);
        
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 10, 0));

        // --- 2. MENU GRID (TENGAH) ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        FlowPane menuContainer = new FlowPane();
        menuContainer.setHgap(15);
        menuContainer.setVgap(15);
        menuContainer.setPadding(new Insets(10));
        menuContainer.setAlignment(Pos.TOP_LEFT);

        List<Menu> dbMenus = DatabaseHelper.getAllMenu();
        
        if (dbMenus.isEmpty()) {
            Label emptyLabel = new Label("Menu kosong.");
            menuContainer.getChildren().add(emptyLabel);
        } else {
            for (Menu m : dbMenus) {
                menuContainer.getChildren().add(createMenuButton(m));
            }
        }
        
        scrollPane.setContent(menuContainer);
        root.setCenter(scrollPane);

        // --- 3. KERANJANG (KANAN) ---
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(15));
        rightPane.setPrefWidth(350);
        rightPane.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");

        Label cartTitle = new Label("Daftar Pesanan");
        cartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // --- PILIH PELANGGAN (BARU) ---
        VBox customerBox = new VBox(5);
        Label lblCust = new Label("Pelanggan:");
        
        cmbCustomer = new ComboBox<>();
        cmbCustomer.setPromptText("Pilih Pelanggan (Opsional)");
        cmbCustomer.setMaxWidth(Double.MAX_VALUE);
        
        // Load data pelanggan ke ComboBox
        refreshCustomerList();
        
        // Tombol Reset Pelanggan (Jadi Guest/Tamu)
        Button btnResetCust = new Button("Reset (Tamu)");
        btnResetCust.setStyle("-fx-font-size: 10px;");
        btnResetCust.setOnAction(e -> cmbCustomer.getSelectionModel().clearSelection());
        
        HBox custHeader = new HBox(10, lblCust, btnResetCust);
        custHeader.setAlignment(Pos.CENTER_LEFT);
        
        customerBox.getChildren().addAll(custHeader, cmbCustomer);
        // ------------------------------

        table = new TableView<>();
        table.setItems(cartData);
        table.setPlaceholder(new Label("Keranjang kosong"));
        VBox.setVgrow(table, Priority.ALWAYS); 
        
        TableColumn<CartItem, String> colName = new TableColumn<>("Item");
        colName.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colName.setPrefWidth(140);

        TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colQty.setPrefWidth(60);
        
        TableColumn<CartItem, Integer> colTotal = new TableColumn<>("Subtotal");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(100);

        table.getColumns().addAll(colName, colQty, colTotal);

        Button btnCheckout = new Button("PROSES PEMBAYARAN");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setPrefHeight(40);
        btnCheckout.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCheckout.setOnAction(e -> processCheckout());

        Button btnClear = new Button("Kosongkan Keranjang");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand;");
        btnClear.setOnAction(e -> {
            cartData.clear();
            updateGrandTotal();
        });

        lblGrandTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblGrandTotal.setAlignment(Pos.CENTER_RIGHT);
        lblGrandTotal.setMaxWidth(Double.MAX_VALUE);

        // Tambahkan customerBox ke layout
        rightPane.getChildren().addAll(cartTitle, customerBox, table, lblGrandTotal, btnClear, btnCheckout);
        
        root.setRight(rightPane);

        return root;
    }

    // --- HELPER METHODS ---
    
    // Method untuk load ulang list pelanggan (bisa dipanggil dari luar jika ada fitur tambah pelanggan)
    public void refreshCustomerList() {
        List<CustomerModel> customers = DatabaseHelper.getAllCustomers();
        cmbCustomer.setItems(FXCollections.observableArrayList(customers));
    }

    private Button createMenuButton(Menu m) {
        Button btn = new Button(m.getNamaMenu() + "\nRp " + m.getHarga());
        btn.setPrefSize(140, 90);
        btn.setWrapText(true);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #adb5bd; -fx-border-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5;"));

        btn.setOnAction(e -> addToCart(m));
        return btn;
    }

    private void addToCart(Menu menu) {
        for (CartItem item : cartData) {
            if (item.getNama().equals(menu.getNamaMenu())) {
                item.addQty(1);
                table.refresh(); 
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

    private void processCheckout() {
        if (cartData.isEmpty()) {
            showAlert(AlertType.WARNING, "Peringatan", "Keranjang masih kosong!");
            return;
        }

        // Ambil Pelanggan yang dipilih
        CustomerModel selectedCust = cmbCustomer.getValue();
        String custName = (selectedCust != null) ? selectedCust.getNamaPelanggan() : "Tamu (Guest)";
        String custId = (selectedCust != null) ? selectedCust.getIdPelanggan() : null;

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Pembayaran");
        confirm.setHeaderText("Pelanggan: " + custName + "\nTotal: " + lblGrandTotal.getText());
        confirm.setContentText("Lanjutkan proses transaksi?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                
                String currentStaff = UserSession.getStaffId();
                if (currentStaff == null) currentStaff = "KS001"; 

                // Panggil saveTransaction dengan parameter custId
                boolean success = DatabaseHelper.saveTransaction(currentStaff, custId, new ArrayList<>(cartData));

                if (success) {
                    showAlert(AlertType.INFORMATION, "Sukses", "Transaksi berhasil disimpan!");
                    cartData.clear();
                    updateGrandTotal();
                    cmbCustomer.getSelectionModel().clearSelection(); // Reset pilihan pelanggan
                } else {
                    showAlert(AlertType.ERROR, "Gagal", "Terjadi kesalahan saat menyimpan ke database.");
                }
            }
        });
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}