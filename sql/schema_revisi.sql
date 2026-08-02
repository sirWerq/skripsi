CREATE DATABASE IF NOT EXISTS db_warung_haryati;
USE db_warung_haryati;

-- 1. Tabel Pemilik Warung Haryati
CREATE TABLE IF NOT EXISTS pemilik_warung_haryati (
    id_pemilik VARCHAR(50) PRIMARY KEY,
    nama_pemilik VARCHAR(100) NOT NULL,
    kata_sandi VARCHAR(255) NOT NULL
);

-- 2. Tabel Login
CREATE TABLE IF NOT EXISTS login (
    id_login VARCHAR(50) PRIMARY KEY,
    id_pemilik VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_pemilik) REFERENCES pemilik_warung_haryati(id_pemilik)
);

-- 3. Tabel Logout
CREATE TABLE IF NOT EXISTS logout (
    id_logout VARCHAR(50) PRIMARY KEY,
    id_pemilik VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_pemilik) REFERENCES pemilik_warung_haryati(id_pemilik)
);

-- 4. Tabel Dashboard
CREATE TABLE IF NOT EXISTS dashboard (
    id_dashboard INT AUTO_INCREMENT PRIMARY KEY,
    ringkasan_produk TEXT,
    ringkasan_transaksi TEXT,
    file_dataset_excel VARCHAR(255)
);

-- 5. Tabel Produk
CREATE TABLE IF NOT EXISTS produk (
    id_produk VARCHAR(50) PRIMARY KEY,
    nama_barang VARCHAR(100) NOT NULL,
    stok INT NOT NULL DEFAULT 0,
    harga_beli DOUBLE NOT NULL,
    harga_jual DOUBLE NOT NULL
);

-- 6. Tabel Transaksi
CREATE TABLE IF NOT EXISTS transaksi (
    transaksi_id VARCHAR(50) PRIMARY KEY,
    tanggal DATE NOT NULL,
    total_belanja DOUBLE NOT NULL
);

-- 7. Tabel Detail Transaksi
CREATE TABLE IF NOT EXISTS detail_transaksi (
    detail_id VARCHAR(50) PRIMARY KEY,
    transaksi_id VARCHAR(50) NOT NULL,
    produk_id VARCHAR(50) NOT NULL,
    kuantitas INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    laba DOUBLE NOT NULL,
    FOREIGN KEY (transaksi_id) REFERENCES transaksi(transaksi_id),
    FOREIGN KEY (produk_id) REFERENCES produk(id_produk)
);

-- 8. Tabel Proses Analisis FP-Growth
CREATE TABLE IF NOT EXISTS proses_analisis_fp_growth (
    id_analisis VARCHAR(50) PRIMARY KEY,
    min_support DOUBLE NOT NULL,
    min_confidence DOUBLE NOT NULL
);

-- 9. Tabel Laporan
CREATE TABLE IF NOT EXISTS laporan (
    id_laporan VARCHAR(50) PRIMARY KEY,
    jenis_laporan VARCHAR(100) NOT NULL,
    periode_awal DATE,
    periode_akhir DATE,
    tanggal_cetak DATETIME NOT NULL
);

-- Menambahkan Data Default untuk Pemilik
INSERT IGNORE INTO pemilik_warung_haryati (id_pemilik, nama_pemilik, kata_sandi) VALUES 
('U-7B2A9C1D', 'Haryati', 'admin123');
