package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox; // Import HBox karena LoginPanel mengembalikan HBox
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Buat object LoginPanel
            LoginPanel loginPage = new LoginPanel();

            // 2. Ambil root layout dari LoginPanel (method yang kita buat tadi)
            HBox root = loginPage.getLoginView();

            // 3. Masukkan ke Scene
            Scene scene = new Scene(root, 700, 450);
            
            // 4. Setup Stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("AOL Kitchen - Sistem Kasir");
            primaryStage.setResizable(false); // Opsional: agar ukuran fix
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}