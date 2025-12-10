-- ==========================================
-- 4. INSERT DATA DUMMY (CONTOH DATA)
-- ==========================================

-- Input Kategori
INSERT INTO Kategori VALUES 
('KAT01', 'Makanan Berat'),
('KAT02', 'Minuman'),
('KAT03', 'Snack');

-- Input Menu
INSERT INTO Menu VALUES
('MN001', 'Nasi Goreng Spesial', 25000, 'KAT01'),
('MN002', 'Ayam Bakar Madu', 30000, 'KAT01'),
('MN003', 'Es Teh Manis', 5000, 'KAT02'),
('MN004', 'Kentang Goreng', 15000, 'KAT03');

-- Input Kasir (PENTING UNTUK TES LOGIN)
-- ID: KS001, Pass: 12345
INSERT INTO Kasir (id_kasir, nama_kasir, password) VALUES 
('KS001', 'Budi Santoso', '12345'),
('KS002', 'Siti Aminah', 'admin123');

-- Input Pelanggan
INSERT INTO Pelanggan VALUES
('PL001', 'Joko Anwar', 'Gold', '08123456789'),
('PL002', 'Rina Nose', 'Silver', NULL);

-- Input Transaksi Contoh
INSERT INTO Transaksi VALUES
('TRX-001', 'PL001', 'KS001', '2023-10-27 12:30:00', '12');

-- Input Detail Transaksi Contoh
-- Beli 2 Nasi Goreng (2 x 25.000 = 50.000)
INSERT INTO Detail_Pesanan VALUES
('TRX-001', 'MN001', 2, 50000, 25000); 

-- Beli 1 Es Teh (1 x 5.000 = 5.000)
INSERT INTO Detail_Pesanan VALUES
('TRX-001', 'MN003', 1, 5000, 5000);