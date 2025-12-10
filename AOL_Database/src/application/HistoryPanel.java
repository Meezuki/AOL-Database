package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.TransactionModel;
import util.DatabaseHelper;

public class HistoryPanel {

    private TableView<TransactionModel> table;
    private ObservableList<TransactionModel> data;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // HEADER (Hanya Judul, tombol kembali dihapus)
        Label lblTitle = new Label("Riwayat Transaksi");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        VBox header = new VBox(10, lblTitle);
        header.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(header);

        // TABEL
        table = new TableView<>();
        
        // Setup Kolom
        TableColumn<TransactionModel, String> colNota = new TableColumn<>("No Nota");
        colNota.setCellValueFactory(new PropertyValueFactory<>("noNota"));
        colNota.setPrefWidth(200);

        TableColumn<TransactionModel, String> colDate = new TableColumn<>("Waktu Transaksi");
        colDate.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colDate.setPrefWidth(200);

        TableColumn<TransactionModel, String> colKasir = new TableColumn<>("Kasir");
        colKasir.setCellValueFactory(new PropertyValueFactory<>("namaKasir"));
        colKasir.setPrefWidth(150);

        TableColumn<TransactionModel, Integer> colTotal = new TableColumn<>("Total Belanja");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalBelanja"));
        colTotal.setPrefWidth(150);

        table.getColumns().addAll(colNota, colDate, colKasir, colTotal);
        
        // Load Data Awal
        refreshData();
        
        root.setCenter(table);

        return root;
    }

    // Method ini dipanggil oleh MainLayout saat Tab diklik
    public void refreshData() {
        if (table != null) {
            data = FXCollections.observableArrayList(DatabaseHelper.getTransactionHistory());
            table.setItems(data);
            table.refresh();
            System.out.println("History data refreshed!");
        }
    }
}