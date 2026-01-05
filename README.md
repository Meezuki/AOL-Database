<img width="1148" height="677" alt="image" src="https://github.com/user-attachments/assets/4cb4310e-7da3-4e56-af38-bf04b6769da8" />


**VM Arguments**

--module-path "C:\JavaFX\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics

**JavaFX 25 path**
C:\JavaFX\javafx-sdk-25.0.1\lib
Menggunakan JDK 25, mysql connector 9.5.0


**-- DIBAWAH DESKRIPSI UNTUK MATKUL OOP --**

AOL Kitchen POS System adalah aplikasi manajemen transaksi restoran berbasis desktop yang dibangun menggunakan JavaFX dan basis data relasional MySQL. Aplikasi ini dirancang untuk mendigitalkan seluruh alur operasional restoran, mulai dari autentikasi staf, manajemen inventaris menu, pendataan pelanggan, hingga pelaporan analisis bisnis secara real-time.


**CARA MENJALANKAN**

Cara Menjalankan Aplikasi
Impor Database: Jalankan file SQL create_table pada MySQL (XAMPP/MySQL Workbench).
Konfigurasi Koneksi: Pastikan kredensial database di DatabaseConnection.java (URL, User, Pass) sudah sesuai.
Jalankan Main: Run class Main.java dari IDE  (Eclipse/IntelliJ/VS Code).
Login: Gunakan ID Kasir dan Password yang terdaftar (Default: KS001 / admin123).

**Daftar Class dan Fungsinya**

util.DatabaseConnection: Mengelola koneksi driver ke MySQL.
util.DatabaseHelper: Berisi seluruh logika SQL (CRUD, Login, Simpan Transaksi, Reporting).
application.MainLayout: Wadah utama yang menampung sistem navigasi berbasis TabPane.
application.DashboardPanel: Antarmuka kasir untuk memproses pesanan dan pembayaran.
application.ReportPanel: Menampilkan dashboard statistik bisnis (Top Menu, Kinerja Kasir, dll).
model (CartItem, Menu, Kasir, dll): Class POJO (Plain Old Java Object) untuk representasi objek data.

**Konsep OOP yang Dipakai**
Aplikasi ini mengimplementasikan konsep dasar Pemrograman Berorientasi Objek:
Encapsulation: Menggunakan akses private pada field model dan menyediakannya melalui getter methods.
Modularitas: Membagi setiap halaman menjadi class Panel yang berbeda untuk memudahkan pemeliharaan kode.
Inheritance: Menggunakan pewarisan dari class javafx.application.Application untuk menjalankan UI.

