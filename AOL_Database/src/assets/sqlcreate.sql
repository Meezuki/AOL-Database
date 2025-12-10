-- A. Tabel Kategori
CREATE TABLE Kategori (
    id_kategori CHAR(5) PRIMARY KEY,
    nama_kategori VARCHAR(50) NOT NULL
);

-- B. Tabel Menu
CREATE TABLE Menu (
    kode_menu CHAR(5) PRIMARY KEY,
    nama_menu VARCHAR(100) NOT NULL, -- 100 char agar muat nama panjang
    harga_satuan INT NOT NULL,
    id_kategori CHAR(5) NOT NULL,
    -- Relasi ke Kategori
    FOREIGN KEY (id_kategori) REFERENCES Kategori(id_kategori)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- C. Tabel Pelanggan
CREATE TABLE Pelanggan (
    id_pelanggan VARCHAR(15) PRIMARY KEY,
    nama_pelanggan VARCHAR(50) NOT NULL,
    tipe_membership VARCHAR(20),
    no_telp_pelanggan VARCHAR(20) -- Boleh NULL
);

-- D. Tabel Kasir (UPDATE: Ada kolom password)
CREATE TABLE Kasir (
    id_kasir CHAR(5) PRIMARY KEY,
    nama_kasir VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL DEFAULT '12345' -- Password untuk Login JavaFX
);

-- ==========================================
-- 3. MEMBUAT TABEL TRANSAKSI
-- ==========================================

-- E. Tabel Transaksi (Header)
CREATE TABLE Transaksi (
    no_nota VARCHAR(20) PRIMARY KEY,
    id_pelanggan VARCHAR(15), -- Boleh NULL (Tamu biasa)
    id_kasir CHAR(5) NOT NULL,
    tanggal_transaksi DATETIME NOT NULL, -- Wajib DATETIME
    no_meja CHAR(5),
    
    -- Relasi
    FOREIGN KEY (id_pelanggan) REFERENCES Pelanggan(id_pelanggan)
        ON UPDATE CASCADE ON DELETE SET NULL,
    FOREIGN KEY (id_kasir) REFERENCES Kasir(id_kasir)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- F. Tabel Detail Pesanan
CREATE TABLE Detail_Pesanan (
    no_nota VARCHAR(15),
    kode_menu CHAR(5),
    jumlah INT NOT NULL,
    subtotal INT NOT NULL,
    harga_satuan_saat_itu INT NOT NULL, -- Audit harga
    
    -- Primary Key Gabungan (Satu menu hanya muncul sekali per nota)
    PRIMARY KEY (no_nota, kode_menu),
    
    -- Relasi
    FOREIGN KEY (no_nota) REFERENCES Transaksi(no_nota)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (kode_menu) REFERENCES Menu(kode_menu)
        ON UPDATE CASCADE ON DELETE RESTRICT
);