package application;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

public class MainLayout {

    public BorderPane getView() {
        BorderPane root = new BorderPane();

        // 1. Inisialisasi Panel
        DashboardPanel dashboard = new DashboardPanel();
        HistoryPanel history = new HistoryPanel();
        ManageMenuPanel manageMenu = new ManageMenuPanel();
        ManageCategoryPanel manageCategory = new ManageCategoryPanel();
        ManageCustomerPanel manageCustomer = new ManageCustomerPanel(); // BARU

        // 2. Setup TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // --- DEFINE TABS ---
        
        // Tab 1: Kasir
        Tab tabKasir = new Tab("Kasir / POS");
        tabKasir.setContent(dashboard.getView());
        // Saat kembali ke kasir, refresh dropdown pelanggan agar data baru muncul
        tabKasir.setOnSelectionChanged(e -> {
            if(tabKasir.isSelected()) dashboard.refreshCustomerList();
        });

        // Tab 2: Riwayat
        Tab tabHistory = new Tab("Riwayat");
        tabHistory.setContent(history.getView());
        tabHistory.setOnSelectionChanged(e -> {
            if (tabHistory.isSelected()) history.refreshData();
        });

        // Tab 3: Menu
        Tab tabMenu = new Tab("Menu");
        tabMenu.setContent(manageMenu.getView());

        // Tab 4: Kategori
        Tab tabCategory = new Tab("Kategori");
        tabCategory.setContent(manageCategory.getView());
        
        // Tab 5: Pelanggan (BARU)
        Tab tabCustomer = new Tab("Pelanggan");
        tabCustomer.setContent(manageCustomer.getView());

        // Add All Tabs
        tabPane.getTabs().addAll(tabKasir, tabHistory, tabMenu, tabCategory, tabCustomer);
        
        root.setCenter(tabPane);
        return root;
    }
}