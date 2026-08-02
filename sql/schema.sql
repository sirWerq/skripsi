CREATE DATABASE IF NOT EXISTS db_warung_haryati;
USE db_warung_haryati;

-- Reset Tables (Hapus tabel lama agar struktur baru VARCHAR bisa diterapkan)
DROP TABLE IF EXISTS detail_transaksi;
DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS produk;
DROP TABLE IF EXISTS users;

-- Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Tabel Produk
CREATE TABLE IF NOT EXISTS produk (
    id VARCHAR(50) PRIMARY KEY,
    nama_barang VARCHAR(100) NOT NULL,
    harga_beli DOUBLE NOT NULL,
    harga_jual DOUBLE NOT NULL
);

-- Tabel Transaksi
CREATE TABLE IF NOT EXISTS transaksi (
    id VARCHAR(50) PRIMARY KEY,
    tanggal DATE NOT NULL,
    total DOUBLE NOT NULL
);

-- Tabel Detail Transaksi
CREATE TABLE IF NOT EXISTS detail_transaksi (
    id VARCHAR(50) PRIMARY KEY,
    transaksi_id VARCHAR(50),
    produk_id VARCHAR(50),
    kuantitas INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    laba DOUBLE NOT NULL,
    FOREIGN KEY (transaksi_id) REFERENCES transaksi(id),
    FOREIGN KEY (produk_id) REFERENCES produk(id)
);

-- User Default
-- ID random: U-7B2A9C1D
INSERT IGNORE INTO users (id, username, password) VALUES 
('U-7B2A9C1D', 'admin', 'admin123');
