package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class LoginPanel {

    // Method ini mengembalikan layout utama (HBox) agar bisa dipanggil oleh Main
    public HBox getLoginView() {

        // --- 1. PANEL KIRI (Branding Perusahaan) ---
        VBox leftPane = new VBox(20); 
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #2D3447;"); 
        
        // Load Gambar
        // Pastikan folder assets ada di folder resources (src/main/resources/assets/logo.png)
        try {
            Image image = new Image(getClass().getResourceAsStream("/assets/logo.png"));
            ImageView logoImage = new ImageView(image);
            logoImage.setFitHeight(100);
            logoImage.setFitWidth(180);
            leftPane.getChildren().add(logoImage);
        } catch (Exception e) {
            System.out.println("Gambar tidak ditemukan, pastikan path '/assets/logo.png' benar.");
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
        txtStaffId.setPromptText("Masukkan Staff ID");
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
        
        // Aksi Tombol Login
        btnLogin.setOnAction(e -> {
            String id = txtStaffId.getText();
            String pass = txtPassword.getText();
            
            // 1. Validasi Input Kosong
            if (id.isEmpty() || pass.isEmpty()) {
                msgLabel.setText("Staff ID dan Password harus diisi!");
                msgLabel.setTextFill(Color.RED);
                return; // Stop proses
            } 
            
            // 2. Cek ke Database
            msgLabel.setText("Memeriksa data..."); // Feedback visual
            msgLabel.setTextFill(Color.BLUE);

            // Menjalankan cek login
            boolean isValid = DatabaseHelper.validateLogin(id, pass);

            if (isValid) {
                msgLabel.setText("Login Berhasil!");
                msgLabel.setTextFill(Color.GREEN);
                
                // TODO: Pindah ke Scene Utama (Dashboard)
                // Contoh logika pindah scene sederhana (jika diperlukan disini):
                // Stage stage = (Stage) btnLogin.getScene().getWindow();
                // stage.setScene(new Scene(new DashboardPanel().getView()));
                
            } else {
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

        // KEMBALIKAN ROOT (HBox)
        return root;
    }
}