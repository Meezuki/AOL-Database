package application;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ReportModel;
import util.DatabaseHelper;

public class ReportPanel {

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- HEADER ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitle = new Label("Laporan Analisis Bisnis");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Button btnRefresh = new Button("Refresh Data");
        
        header.getChildren().addAll(lblTitle, btnRefresh);
        root.setTop(header);
        
        // --- CONTENT (GRID 2x2) ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 0, 0, 0));
        
        // Agar grid responsive mengisi layar
        grid.prefWidthProperty().bind(root.widthProperty());
        
        // 1. Tabel Meja Terlaris
        VBox boxMeja = createReportBox("Meja Terpopuler", "No Meja", "Frekuensi");
        TableView<ReportModel> tblMeja = (TableView<ReportModel>) boxMeja.getChildren().get(1);
        
        // 2. Tabel Kategori Terlaris
        VBox boxKategori = createReportBox("Kategori Terlaris (Omzet)", "Kategori", "Total (Rp)");
        TableView<ReportModel> tblKategori = (TableView<ReportModel>) boxKategori.getChildren().get(1);
        
        // 3. Tabel Menu Top 5
        VBox boxMenu = createReportBox("Top 5 Menu Favorit", "Nama Menu", "Terjual (Porsi)");
        TableView<ReportModel> tblMenu = (TableView<ReportModel>) boxMenu.getChildren().get(1);
        
        // 4. Tabel Kinerja Staff
        VBox boxStaff = createReportBox("Kinerja Kasir", "Nama Staff", "Jml Transaksi");
        TableView<ReportModel> tblStaff = (TableView<ReportModel>) boxStaff.getChildren().get(1);

        // Tambahkan ke Grid (Kolom, Baris)
        grid.add(boxMeja, 0, 0);
        grid.add(boxKategori, 1, 0);
        grid.add(boxMenu, 0, 1);
        grid.add(boxStaff, 1, 1);
        
        // Setup Refresh Action
        Runnable loadData = () -> {
            tblMeja.setItems(FXCollections.observableArrayList(DatabaseHelper.getHotTables()));
            tblKategori.setItems(FXCollections.observableArrayList(DatabaseHelper.getBestCategory()));
            tblMenu.setItems(FXCollections.observableArrayList(DatabaseHelper.getTopMenu()));
            tblStaff.setItems(FXCollections.observableArrayList(DatabaseHelper.getTopStaff()));
        };
        
        btnRefresh.setOnAction(e -> loadData.run());
        
        // Load Data Pertama Kali
        loadData.run();

        // Bungkus Grid dengan ScrollPane (jika layar kecil)
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scroll);

        return root;
    }
    
    // Helper untuk membuat Kotak Laporan secara cepat (Factory Method)
    @SuppressWarnings("unchecked")
    private VBox createReportBox(String title, String col1Header, String col2Header) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lbl.setTextFill(javafx.scene.paint.Color.DARKBLUE);
        
        TableView<ReportModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Agar kolom memenuhi tabel
        table.setPrefHeight(200); // Tinggi fix agar rapi
        
        TableColumn<ReportModel, String> col1 = new TableColumn<>(col1Header);
        col1.setCellValueFactory(new PropertyValueFactory<>("label"));
        
        TableColumn<ReportModel, Integer> col2 = new TableColumn<>(col2Header);
        col2.setCellValueFactory(new PropertyValueFactory<>("value"));
        
        table.getColumns().addAll(col1, col2);
        
        box.getChildren().addAll(lbl, table);
        return box;
    }
}