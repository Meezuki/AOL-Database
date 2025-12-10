package application;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.CategoryModel;
import util.DatabaseHelper;

public class ManageCategoryPanel {

    private TextField txtId, txtNama;
    private TableView<CategoryModel> table;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- KIRI: FORM ---
        VBox form = new VBox(15);
        form.setPrefWidth(300);
        form.setPadding(new Insets(0, 20, 0, 0));

        Label lbl = new Label("Kelola Kategori");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        txtId = new TextField(); 
        txtId.setPromptText("ID Kategori (Cth: KAT01)");
        
        txtNama = new TextField(); 
        txtNama.setPromptText("Nama Kategori (Cth: Makanan)");

        Button btnAdd = new Button("Tambah");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        btnAdd.setOnAction(e -> {
            if (DatabaseHelper.insertCategory(txtId.getText(), txtNama.getText())) {
                refresh(); clear();
            } else showAlert("Gagal", "ID mungkin duplikat.");
        });

        Button btnUpdate = new Button("Update");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setStyle("-fx-background-color: #ffc107;");
        btnUpdate.setOnAction(e -> {
            if (DatabaseHelper.updateCategory(txtId.getText(), txtNama.getText())) {
                refresh(); clear();
            } else showAlert("Gagal", "Update gagal.");
        });

        Button btnDelete = new Button("Hapus");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> {
            if (DatabaseHelper.deleteCategory(txtId.getText())) {
                refresh(); clear();
            } else showAlert("Gagal", "Kategori sedang dipakai di menu.");
        });
        
        Button btnClear = new Button("Clear");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> clear());

        form.getChildren().addAll(lbl, new Label("ID:"), txtId, new Label("Nama:"), txtNama, new Label(""), btnAdd, btnUpdate, btnDelete, btnClear);

        // --- KANAN: TABEL ---
        table = new TableView<>();
        TableColumn<CategoryModel, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<CategoryModel, String> colNama = new TableColumn<>("Nama Kategori");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(150);
        
        table.getColumns().addAll(colId, colNama);
        
        table.getSelectionModel().selectedItemProperty().addListener((o, old, val) -> {
            if (val != null) {
                txtId.setText(val.getId());
                txtNama.setText(val.getNama());
                txtId.setDisable(true);
            }
        });

        refresh();
        
        root.setLeft(form);
        root.setCenter(table);
        return root;
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(DatabaseHelper.getCategoryList()));
    }

    private void clear() {
        txtId.clear(); txtId.setDisable(false);
        txtNama.clear();
        table.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title); a.setContentText(msg); a.show();
    }
}