package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import model.CustomerModel;
import util.DatabaseHelper;

public class ManageCustomerPanel {

    private TextField txtId, txtNama, txtTelp;
    private ComboBox<String> cmbTipe;
    private TableView<CustomerModel> table;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- FORM INPUT (KIRI) ---
        VBox form = new VBox(15);
        form.setPrefWidth(300);
        form.setPadding(new Insets(0, 20, 0, 0));

        Label lbl = new Label("Kelola Pelanggan");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        txtId = new TextField(); 
        txtId.setPromptText("ID Pelanggan (Cth: P001)");
        
        txtNama = new TextField(); 
        txtNama.setPromptText("Nama Pelanggan");
        
        txtTelp = new TextField(); 
        txtTelp.setPromptText("No Telepon (Opsional)");
        
        // Dropdown Tipe Membership
        cmbTipe = new ComboBox<>();
        cmbTipe.getItems().addAll("Regular", "Silver", "Gold", "Platinum");
        cmbTipe.setPromptText("Pilih Membership");
        cmbTipe.setMaxWidth(Double.MAX_VALUE);

        Button btnAdd = new Button("Tambah");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        btnAdd.setOnAction(e -> actionInsert());

        Button btnUpdate = new Button("Update");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setStyle("-fx-background-color: #ffc107;");
        btnUpdate.setOnAction(e -> actionUpdate());

        Button btnDelete = new Button("Hapus");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> actionDelete());
        
        Button btnClear = new Button("Clear");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> clearForm());

        form.getChildren().addAll(lbl, 
            new Label("ID:"), txtId, 
            new Label("Nama:"), txtNama, 
            new Label("Membership:"), cmbTipe,
            new Label("No Telp:"), txtTelp, 
            new Label(""), btnAdd, btnUpdate, btnDelete, btnClear
        );

        // --- TABEL (KANAN) ---
        table = new TableView<>();
        
        TableColumn<CustomerModel, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idPelanggan"));
        
        TableColumn<CustomerModel, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colNama.setPrefWidth(150);
        
        TableColumn<CustomerModel, String> colTipe = new TableColumn<>("Membership");
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipeMembership"));
        
        TableColumn<CustomerModel, String> colTelp = new TableColumn<>("Telepon");
        colTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        
        table.getColumns().addAll(colId, colNama, colTipe, colTelp);
        
        // Listener Klik Tabel
        table.getSelectionModel().selectedItemProperty().addListener((o, old, val) -> {
            if (val != null) {
                txtId.setText(val.getIdPelanggan());
                txtNama.setText(val.getNamaPelanggan());
                txtTelp.setText(val.getNoTelp());
                cmbTipe.setValue(val.getTipeMembership());
                txtId.setDisable(true); // Kunci ID saat edit
            }
        });

        refresh();
        
        root.setLeft(form);
        root.setCenter(table);
        return root;
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(DatabaseHelper.getAllCustomers()));
    }

    private void clearForm() {
        txtId.clear(); txtId.setDisable(false);
        txtNama.clear(); txtTelp.clear();
        cmbTipe.getSelectionModel().clearSelection();
        table.getSelectionModel().clearSelection();
    }
    
    private void actionInsert() {
        if(txtId.getText().isEmpty() || txtNama.getText().isEmpty()) {
            showAlert("Error", "ID dan Nama wajib diisi."); return;
        }
        if(DatabaseHelper.insertCustomer(txtId.getText(), txtNama.getText(), cmbTipe.getValue(), txtTelp.getText())) {
            refresh(); clearForm(); showAlert("Sukses", "Pelanggan ditambah.");
        } else showAlert("Gagal", "ID mungkin duplikat.");
    }
    
    private void actionUpdate() {
        if(DatabaseHelper.updateCustomer(txtId.getText(), txtNama.getText(), cmbTipe.getValue(), txtTelp.getText())) {
            refresh(); clearForm(); showAlert("Sukses", "Data diupdate.");
        } else showAlert("Gagal", "Update gagal.");
    }
    
    private void actionDelete() {
        if(DatabaseHelper.deleteCustomer(txtId.getText())) {
            refresh(); clearForm(); showAlert("Sukses", "Data dihapus.");
        } else showAlert("Gagal", "Pelanggan sudah pernah transaksi.");
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.show();
    }
}