package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

// Import Helper Database
import util.DatabaseHelper;

public class LoginPanel {

    public HBox getLoginView() {

        // --- 1. PANEL KIRI (Branding Perusahaan) ---
        VBox leftPane = new VBox(20); 
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #2D3447;"); 
        
        // Load Gambar Logo
        try {
            // Pastikan file ada di src/main/resources/assets/logo.png
            Image image = new Image(getClass().getResourceAsStream("/assets/logo.png"));
            ImageView logoImage = new ImageView(image);
            logoImage.setFitHeight(100);
            logoImage.setFitWidth(180);
            leftPane.getChildren().add(logoImage);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan. Cek folder resources/assets.");
        }

        Label companyLabel = new Label("AOL Kitchen");
        companyLabel.setTextFill(Color.WHITE);
        companyLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        
        Label sloganLabel = new Label("Sistem Kasir Restoran");
        sloganLabel.setTextFill(Color.LIGHTGRAY);
        sloganLabel.setFont(Font.font("Arial", 14));

        leftPane.getChildren().addAll(companyLabel, sloganLabel);


        // --- 2. PANEL KANAN (Form Login) ---
        VBox rightPane = new VBox(15); 
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(40));
        rightPane.setStyle("-fx-background-color: #FFFFFF;");

        Label loginTitle = new Label("Staff Login");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginTitle.setTextFill(Color.web("#2D3447"));

        TextField txtStaffId = new TextField();
        txtStaffId.setPromptText("Masukkan Staff ID (Contoh: KS001)");
        txtStaffId.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Masukkan Password");
        txtPassword.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");

        Button btnLogin = new Button("LOGIN");
        btnLogin.setPrefWidth(200);
        btnLogin.setStyle(
            "-fx-background-color: #ffc107; " +
            "-fx-text-fill: #2D3447; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );

        Label msgLabel = new Label();
        
        // --- LOGIKA TOMBOL LOGIN ---
        btnLogin.setOnAction(e -> {
            String id = txtStaffId.getText();
            String pass = txtPassword.getText();
            
            // 1. Validasi Input Kosong
            if (id.isEmpty() || pass.isEmpty()) {
                msgLabel.setText("Staff ID dan Password harus diisi!");
                msgLabel.setTextFill(Color.RED);
                return; 
            } 
            
            msgLabel.setText("Memeriksa data...");
            msgLabel.setTextFill(Color.BLUE);

            // 2. Cek ke Database via Helper
            boolean isValid = DatabaseHelper.validateLogin(id, pass);

            if (isValid) {
                // === LOGIN SUKSES ===
            	
            	
            	util.UserSession.setSession(id, "Staff");
                msgLabel.setText("Login Berhasil!");
                msgLabel.setTextFill(Color.GREEN);
                
                // A. Ambil Stage (Window) saat ini dari tombol login
                Stage currentStage = (Stage) btnLogin.getScene().getWindow();
                
                // B. Siapkan Halaman Dashboard
                DashboardPanel dashboard = new DashboardPanel();
                
                // C. Ganti Scene (Layar) ke Dashboard
                // Ukuran diset lebih besar (900x600) agar muat menu & keranjang
                Scene dashboardScene = new Scene(dashboard.getView(), 900, 600);
                
                currentStage.setScene(dashboardScene);
                currentStage.setTitle("AOL Kitchen - Dashboard Kasir");
                currentStage.centerOnScreen(); // Tengahkan layar
                
            } else {
                // === LOGIN GAGAL ===
                msgLabel.setText("ID atau Password salah!");
                msgLabel.setTextFill(Color.RED);
            }
        });

        rightPane.getChildren().addAll(loginTitle, txtStaffId, txtPassword, btnLogin, msgLabel);


        // --- 3. ROOT LAYOUT ---
        HBox root = new HBox();
        root.getChildren().addAll(leftPane, rightPane);

        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        
        leftPane.setMaxWidth(400); 
        leftPane.setPrefWidth(300);

        return root;
    }
}