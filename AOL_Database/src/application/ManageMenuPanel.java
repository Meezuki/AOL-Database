package application;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Menu;
import util.DatabaseHelper;

public class ManageMenuPanel {

    private TextField txtId, txtNama, txtHarga;
    private ComboBox<String> cmbKategori; // Dropdown Kategori
    private TableView<Menu> table;
    private ObservableList<Menu> menuList;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- 1. FORM INPUT (KIRI) ---
        VBox formPane = new VBox(15);
        formPane.setPrefWidth(320);
        formPane.setPadding(new Insets(0, 20, 0, 0));

        Label lblTitle = new Label("Kelola Menu");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        txtId = new TextField();
        txtId.setPromptText("Kode Menu (Cth: MN001)");
        
        txtNama = new TextField();
        txtNama.setPromptText("Nama Menu");
        
        txtHarga = new TextField();
        txtHarga.setPromptText("Harga (Angka)");
        
        // Setup Dropdown Kategori
        cmbKategori = new ComboBox<>();
        cmbKategori.setPromptText("Pilih Kategori");
        cmbKategori.setMaxWidth(Double.MAX_VALUE);
        loadCategories(); // Ambil data kategori dari DB

        Button btnAdd = new Button("Tambah");
        btnAdd.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        
        Button btnUpdate = new Button("Perbarui");
        btnUpdate.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        
        Button btnDelete = new Button("Hapus");
        btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        
        Button btnClear = new Button("Clear Form");
        btnClear.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> actionInsert());
        btnUpdate.setOnAction(e -> actionUpdate());
        btnDelete.setOnAction(e -> actionDelete());
        btnClear.setOnAction(e -> clearForm());

        formPane.getChildren().addAll(
            lblTitle, 
            new Label("Kode Menu:"), txtId, 
            new Label("Nama Menu:"), txtNama, 
            new Label("Harga:"), txtHarga, 
            new Label("Kategori:"), cmbKategori, // Tambahkan ComboBox ke layar
            new Label(""), btnAdd, btnUpdate, btnDelete, btnClear
        );

        // --- 2. TABEL MENU (KANAN) ---
        table = new TableView<>();
        
        TableColumn<Menu, String> colId = new TableColumn<>("Kode");
        colId.setCellValueFactory(new PropertyValueFactory<>("kodeMenu"));
        
        TableColumn<Menu, String> colNama = new TableColumn<>("Nama Menu");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colNama.setPrefWidth(150);
        
        TableColumn<Menu, Integer> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        
        // Kolom Kategori di Tabel
        TableColumn<Menu, String> colKat = new TableColumn<>("ID Kategori");
        colKat.setCellValueFactory(new PropertyValueFactory<>("kategori")); // Pastikan di model Menu ada getKategori()

        table.getColumns().addAll(colId, colNama, colHarga, colKat);
        
        // Listener saat tabel diklik
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtId.setText(newVal.getKodeMenu());
                txtNama.setText(newVal.getNamaMenu());
                txtHarga.setText(String.valueOf(newVal.getHarga()));
                
                // Set combobox value berdasarkan ID kategori yang ada di menu
                // Kita harus mencari string yang cocok di combobox (misal "KAT01 - Makanan")
                String targetId = newVal.getKategori();
                for (String item : cmbKategori.getItems()) {
                    if (item.startsWith(targetId)) {
                        cmbKategori.setValue(item);
                        break;
                    }
                }
                
                txtId.setDisable(true);
            }
        });

        refreshTable();
        
        root.setLeft(formPane);
        root.setCenter(table);

        return root;
    }

    // --- LOGIC METHODS ---

    private void loadCategories() {
        // Ambil list kategori string ("ID - Nama") dari DatabaseHelper
        ObservableList<String> cats = FXCollections.observableArrayList(DatabaseHelper.getAllCategories());
        cmbKategori.setItems(cats);
    }

    private void refreshTable() {
        menuList = FXCollections.observableArrayList(DatabaseHelper.getAllMenu());
        table.setItems(menuList);
        loadCategories(); // Refresh kategori juga jaga-jaga ada kategori baru
    }

    private void clearForm() {
        txtId.clear();
        txtId.setDisable(false);
        txtNama.clear();
        txtHarga.clear();
        cmbKategori.getSelectionModel().clearSelection();
        cmbKategori.setPromptText("Pilih Kategori");
        table.getSelectionModel().clearSelection();
    }

    // Method bantu untuk mengambil ID Kategori dari string "ID - Nama"
    private String getSelectedCategoryId() {
        String selection = cmbKategori.getValue();
        if (selection == null) return null;
        // Pecah string "KAT01 - Makanan" ambil bagian depan "KAT01"
        return selection.split(" - ")[0];
    }

    private void actionInsert() {
        if (!validateInput()) return;
        
        String catId = getSelectedCategoryId();
        
        if (DatabaseHelper.insertMenu(txtId.getText(), txtNama.getText(), Integer.parseInt(txtHarga.getText()), catId)) {
            showAlert(AlertType.INFORMATION, "Sukses", "Menu berhasil ditambahkan!");
            refreshTable();
            clearForm();
        } else {
            showAlert(AlertType.ERROR, "Gagal", "Kode Menu sudah ada atau Kategori salah.");
        }
    }

    private void actionUpdate() {
        if (!validateInput()) return;
        
        String catId = getSelectedCategoryId();

        if (DatabaseHelper.updateMenu(txtId.getText(), txtNama.getText(), Integer.parseInt(txtHarga.getText()), catId)) {
            showAlert(AlertType.INFORMATION, "Sukses", "Menu berhasil diperbarui!");
            refreshTable();
            clearForm();
        } else {
            showAlert(AlertType.ERROR, "Gagal", "Gagal update database.");
        }
    }

    private void actionDelete() {
        if (txtId.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Pilih Data", "Pilih menu untuk dihapus.");
            return;
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setContentText("Hapus " + txtNama.getText() + "?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (DatabaseHelper.deleteMenu(txtId.getText())) {
                showAlert(AlertType.INFORMATION, "Sukses", "Menu dihapus.");
                refreshTable();
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Gagal", "Menu tidak bisa dihapus.");
            }
        }
    }

    private boolean validateInput() {
        if (txtId.getText().isEmpty() || txtNama.getText().isEmpty() || txtHarga.getText().isEmpty() || cmbKategori.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Semua kolom (termasuk Kategori) harus diisi!");
            return false;
        }
        try {
            Integer.parseInt(txtHarga.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Validasi", "Harga harus angka!");
            return false;
        }
        return true;
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}