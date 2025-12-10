package application;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import util.UserSession; 

public class MainLayout {

    public BorderPane getView() {
        BorderPane root = new BorderPane();

        // 1. Inisialisasi Panel Halaman
        DashboardPanel dashboard = new DashboardPanel();
        HistoryPanel history = new HistoryPanel();
        ManageMenuPanel manageMenu = new ManageMenuPanel(); // Panel Baru

        // 2. Buat TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // --- TAB 1: KASIR ---
        Tab tabKasir = new Tab("Kasir / POS");
        tabKasir.setContent(dashboard.getView());
        tabKasir.setClosable(false);

        // --- TAB 2: RIWAYAT ---
        Tab tabHistory = new Tab("Riwayat Transaksi");
        tabHistory.setContent(history.getView());
        tabHistory.setClosable(false);
        
        tabHistory.setOnSelectionChanged(e -> {
            if (tabHistory.isSelected()) history.refreshData();
        });

        // --- TAB 3: KELOLA MENU (BARU) ---
        Tab tabMenu = new Tab("Kelola Menu");
        tabMenu.setContent(manageMenu.getView());
        tabMenu.setClosable(false);
        
        // Logika: Saat tab menu dipilih, refresh Dashboard (siapa tahu harga berubah)
        tabKasir.setOnSelectionChanged(e -> {
             // Opsional: jika ingin dashboard auto-refresh saat balik dari tab menu
             // Anda perlu menambahkan method refreshMenu() di DashboardPanel nanti.
        });

        // Masukkan Tab
        tabPane.getTabs().addAll(tabKasir, tabHistory, tabMenu);

        // --- LOGOUT BUTTON (BONUS) ---
        // Biasanya TabPane memenuhi layar, tapi kita bisa menambahkan tombol logout di header root jika mau.
        // Untuk sekarang, kita full screen TabPane saja.

        root.setCenter(tabPane);

        return root;
    }
}