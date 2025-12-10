package application;

import javafx.application.Application;
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

public class LoginPanel extends Application {

    @Override
    public void start(Stage primaryStage) {

        // --- 1. PANEL KIRI (Branding Perusahaan) ---
        VBox leftPane = new VBox(20); // Spacing 20px
        leftPane.setAlignment(Pos.CENTER);
        // Style CSS Inline untuk background biru tua
        leftPane.setStyle("-fx-background-color: #2D3447;"); 
        
        // Gambar Logo (Placeholder)
        // Ganti URL string di bawah ini dengan path gambar lokal Anda: "file:src/img/logo.png"
//        String imageUrl = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"; 
//        ImageView logoImage = new ImageView(new Image(imageUrl));
        
        
     // Contoh jika file ada di root project atau resources folder
        Image image = new Image(getClass().getResourceAsStream("/assets/logo.png"));
        ImageView logoImage = new ImageView(image);
        
        
        
        logoImage.setFitHeight(150);
        logoImage.setFitWidth(300);
        
        
     

        // Teks Nama Perusahaan
        Label companyLabel = new Label("PT. MAJU MUNDUR");
        companyLabel.setTextFill(Color.WHITE);
        companyLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        
        Label sloganLabel = new Label("Sistem Manajemen Staff");
        sloganLabel.setTextFill(Color.LIGHTGRAY);
        sloganLabel.setFont(Font.font("Arial", 14));

        leftPane.getChildren().addAll(logoImage, companyLabel, sloganLabel);


        // --- 2. PANEL KANAN (Form Login) ---
        VBox rightPane = new VBox(15); // Spacing 15px
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(40));
        rightPane.setStyle("-fx-background-color: #FFFFFF;");

        // Judul Form
        Label loginTitle = new Label("Staff Login");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginTitle.setTextFill(Color.web("#2D3447"));

        // Input Staff ID
        TextField txtStaffId = new TextField();
        txtStaffId.setPromptText("Masukkan Staff ID");
        txtStaffId.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");

        // Input Password
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Masukkan Password");
        txtPassword.setStyle("-fx-background-radius: 5px; -fx-padding: 10px;");

        // Tombol Login
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

        // Label Pesan Error (Hidden by default)
        Label msgLabel = new Label();
        msgLabel.setTextFill(Color.RED);

        // Aksi Tombol Login
        btnLogin.setOnAction(e -> {
            String id = txtStaffId.getText();
            String pass = txtPassword.getText();
            
            // Logika sederhana untuk demo
            if (id.isEmpty() || pass.isEmpty()) {
                msgLabel.setText("Staff ID dan Password harus diisi!");
            } else {
                msgLabel.setText("Mencoba login sebagai: " + id);
                msgLabel.setTextFill(Color.GREEN);
            }
        });

        rightPane.getChildren().addAll(loginTitle, txtStaffId, txtPassword, btnLogin, msgLabel);


        // --- 3. ROOT LAYOUT (Menggabungkan Kiri & Kanan) ---
        HBox root = new HBox();
        root.getChildren().addAll(leftPane, rightPane);

        // Mengatur agar panel kiri dan kanan membagi ruang 50:50 (atau sesuai preferensi)
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        
        // Opsional: Batasi lebar maksimum panel kiri agar tidak terlalu lebar di layar besar
        leftPane.setMaxWidth(400); 
        leftPane.setPrefWidth(300);


        // --- 4. SETUP SCENE & STAGE ---
        Scene scene = new Scene(root, 700, 450); // Ukuran Window Awal
        primaryStage.setTitle("Aplikasi Staff Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}