-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 16, 2026 at 02:42 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_warung_haryati`
--

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id` varchar(50) NOT NULL,
  `transaksi_id` varchar(50) DEFAULT NULL,
  `produk_id` varchar(50) DEFAULT NULL,
  `kuantitas` int(11) NOT NULL,
  `subtotal` double NOT NULL,
  `laba` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detail_transaksi`
--

INSERT INTO `detail_transaksi` (`id`, `transaksi_id`, `produk_id`, `kuantitas`, `subtotal`, `laba`) VALUES
('DT-000F8452', '000C3F94', 'P-00555AC8', 3, 204000, 18000),
('DT-001A4D85', '014731CC', 'P-03BB2DB6', 3, 6000, 2400),
('DT-00BB7315', '01902023', 'P-04E44892', 1, 5000, 1500),
('DT-00DDE328', '019CC1F8', 'P-05B0748C', 2, 30000, 3000),
('DT-01039273', '01CB7604', 'P-08A1FC9E', 1, 18500, 3000),
('DT-01117BD8', '01E1D784', 'P-094F1C5B', 3, 9300, 1500),
('DT-01363081', '01EA61BA', 'P-0BF94172', 3, 6000, 2400),
('DT-017A65F5', '02009383', 'P-0D0CE5D8', 1, 2000, 800);

-- --------------------------------------------------------

--
-- Table structure for table `produk`
--

CREATE TABLE `produk` (
  `id` varchar(50) NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `harga_beli` double NOT NULL,
  `harga_jual` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produk`
--

INSERT INTO `produk` (`id`, `nama_barang`, `harga_beli`, `harga_jual`) VALUES
('P-00555AC8', 'Indomie Rendang', 2700, 3200),
('P-03BB2DB6', 'Rinso Bubuk 800g', 18500, 21500),
('P-04E44892', 'Minyak Goreng Filma 1L', 15500, 18000),
('P-05B0748C', 'Penyedap Masako Sachet', 500, 1000),
('P-08A1FC9E', 'Beras Rojolele 5kg', 60000, 65000),
('P-094F1C5B', 'Gula Pasir Curah 1kg', 13500, 15000),
('P-0BF94172', 'Sedaap Ayam Jerit', 2800, 3300),
('P-0D0CE5D8', 'Telur Ayam (1kg)', 24500, 27500);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id` varchar(50) NOT NULL,
  `tanggal` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id`, `tanggal`) VALUES
('000C3F94', '2025-10-07'),
('014731CC', '2025-10-11'),
('01902023', '2025-11-02'),
('019CC1F8', '2026-04-16'),
('01CB7604', '2025-12-25'),
('01E1D784', '2026-01-13'),
('01EA61BA', '2025-10-11'),
('02009383', '2025-10-09');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`) VALUES
('U-7B2A9C1D', 'admin', 'admin123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transaksi_id` (`transaksi_id`),
  ADD KEY `produk_id` (`produk_id`);

--
-- Indexes for table `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`transaksi_id`) REFERENCES `transaksi` (`id`),
  ADD CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
