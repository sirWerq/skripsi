# Warung Haryati Management System

Aplikasi manajemen toko dan kasir untuk UMKM Warung Haryati dengan fitur analisis data transaksi (Data Mining) menggunakan algoritma **FP-Growth**. Aplikasi ini dibuat untuk memenuhi project Skripsi dengan judul "Implementasi Pengelompokan Data Transaksi Berdasarkan Tingkat Penjualan pada UMKM Warung Haryati Menggunakan Algoritma FP-Growth".

## Tentang Aplikasi Ini
Aplikasi ini memungkinkan pemilik warung untuk:
- Mengelola data master produk (harga beli, harga jual).
- Mencatat transaksi penjualan harian.
- Menyediakan **4 Laporan Eksekutif & Bisnis** (Laporan Laba Rugi, Barang Terlaris, Analisis FP-Growth, dan Riwayat Transaksi).
- Melakukan analisis transaksi dengan algoritma **FP-Growth** untuk menemukan pola pembelian pelanggan (Asosiasi / *Frequent Itemsets*) serta memberikan **Rekomendasi Strategi Bisnis otomatis** (seperti strategi penataan rak dan bundling produk).

## Arsitektur & Struktur Folder
Aplikasi ini menggunakan pola desain **MVC (Model-View-Controller)** pada sisi JavaFX dan memisahkan proses analitik *Machine Learning* / *Data Mining* menggunakan Python.

```text
skripsi/
├── pom.xml                        # Konfigurasi Maven (Dependencies Java)
├── python/                        # Modul Python untuk algoritma FP-Growth
│   ├── fp_growth.py               # Script utama pemrosesan algoritma FP-Growth
│   ├── requirements.txt           # Daftar library Python yang dibutuhkan
│   └── venv/                      # Python Virtual Environment (di-generate)
├── sql/                           # Skema dan data awal database
│   └── schema.sql                 # Script SQL untuk membuat tabel dan user admin
└── src/
    ├── main/
    │   ├── java/com/warung/haryati/
    │   │   ├── controller/        # Mengontrol logika antar muka (UI/FXML)
    │   │   ├── dao/               # Data Access Object (Query Database)
    │   │   ├── model/             # Representasi entitas tabel database
    │   │   ├── service/           # Bisnis logic, termasuk jembatan Java ke Python
    │   │   ├── util/              # Kelas utilitas (Koneksi DB, Alert Helper)
    │   │   └── App.java           # Entry point aplikasi JavaFX
    │   └── resources/com/warung/haryati/
    │       ├── fxml/              # File layout UI (.fxml)
    │       └── css/               # File styling UI (.css)
```

## Schema Database
Database MySQL bernama `db_warung_haryati` terdiri dari 4 tabel utama:

1. **`users`**
   - Menyimpan akun pengguna (kasir/admin).
   - Kolom: `id` (PK), `username`, `password`.
   - *Default Login: `admin` / `admin123`*
2. **`produk`**
   - Menyimpan data barang dagangan.
   - Kolom: `id` (PK), `nama_barang`, `harga_beli`, `harga_jual`.
3. **`transaksi`**
   - Menyimpan header transaksi penjualan.
   - Kolom: `id` (PK), `tanggal`.
4. **`detail_transaksi`**
   - Menyimpan detail produk dari setiap transaksi.
   - Kolom: `id` (PK), `transaksi_id` (FK), `produk_id` (FK), `kuantitas`, `subtotal`, `laba`.

## Technology & Libraries

### Teknologi Utama
- **Java 25** (Bahasa pemrograman utama backend dan desktop GUI)
- **JavaFX 17** (Framework UI)
- **Python 3.8+** (Engine FP-Growth)
- **MySQL 8** (Database Relasional)
- **Maven** (Dependency Management untuk Java)

### Library / Dependencies
- **Java**:
  - `mysql-connector-java`: Driver koneksi database.
  - `jackson-databind` & `jackson-dataformat-csv`: Parsing data pertukaran dari Python ke Java.
  - `ikonli-javafx` & `ikonli-fontawesome5-pack`: Icon UI modern berbasis FontAwesome.
- **Python**:
  - `pandas`: Manipulasi dan pembacaan dataframe transaksi.
  - `mlxtend`: Implementasi algoritma FP-Growth dan Association Rules.
  - `mysql-connector-python`: Pengambilan data langsung dari database jika diperlukan oleh Python.

## Requirement Aplikasi
Pastikan sistem anda telah terinstall environment berikut agar aplikasi dapat berjalan normal saat di-clone:
- **Java Development Kit (JDK) 25**
- **Apache Maven 3.6+**
- **Python 3.8** atau yang lebih baru
- **MySQL Server** (bisa menggunakan XAMPP, MAMP, atau MySQL Community Server)
- Git (untuk clone repositori)

## Cara Setup Project
Langkah-langkah untuk menyiapkan environment di komputer lokal:

### 1. Persiapan Database
1. Buka MySQL server (contoh: aktifkan module MySQL di XAMPP).
2. Buat database baru bernama `db_warung_haryati`.
3. Import file skema database dengan menjalankan script `sql/schema.sql`.
   *(Script ini secara otomatis akan membuat tabel dan satu user admin).*

### 2. Persiapan Environment Python
Buka terminal/command prompt, arahkan ke folder `python/`, lalu jalankan:

**Windows:**
```bash
cd python
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
```

**Mac/Linux:**
```bash
cd python
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 3. Persiapan Project Java
Kembali ke root direktori proyek, jalankan maven untuk mengunduh semua dependencies Java:
```bash
mvn clean install
```
*(Atau buka file `pom.xml` menggunakan IDE seperti IntelliJ IDEA / Eclipse dan biarkan IDE mengunduh depedencies secara otomatis).*

## Cara Run Aplikasi
Aplikasi ini dapat dijalankan menggunakan beberapa cara:

**Opsi 1: Menggunakan Maven (Terminal)**
Dari root folder proyek, jalankan:
```bash
mvn javafx:run
```

**Opsi 2: Menggunakan IDE (IntelliJ IDEA / VS Code / Eclipse)**
Cari file `src/main/java/com/warung/haryati/App.java`, klik kanan pada file tersebut dan pilih **Run 'App.main()'**.

## Cara Pakai Aplikasi
1. **Login**: Setelah aplikasi terbuka, masukkan Username: `admin` dan Password: `admin123`.
2. **Dashboard**: Menampilkan ringkasan total penjualan, total laba, jumlah produk, dan jumlah transaksi.
3. **Data Produk**: Masuk ke menu "Produk" untuk menambah, mengedit, atau menghapus daftar barang warung.
4. **Transaksi**: Masuk ke menu "Transaksi" untuk mencatat penjualan baru. Masukkan produk dan jumlah, lalu sistem otomatis menghitung subtotal.
5. **Laporan & Analisis Bisnis (4 Laporan Utama)**:
   - Masuk ke menu "Laporan" untuk mengakses 4 tab laporan eksekutif warung:
     - **Laba Rugi & Omset**: Menampilkan rincian transaksi, omset kotor, modal (HPP), dan laba bersih warung per hari.
     - **Barang Terlaris**: Menampilkan peringkat produk dari yang paling laris hingga kurang laris berdasarkan kuantitas dan sumbangan keuntungan.
     - **Analisis FP-Growth**: Menampilkan hasil association rule mining beserta **💡 Rekomendasi Strategi Bisnis otomatis** (misalnya saran bundling atau peletakan rak barang yang berdekatan).
     - **Riwayat Transaksi**: Menampilkan daftar nota / struk penjualan harian.
   - Semua laporan dilengkapi dengan fitur **Export ke CSV/Excel** untuk kemudahan pencetakan dan pelaporan skripsi.
6. **Analisa (FP-Growth) Eksploratif**:
   - Masuk ke menu "Analisa" untuk melakukan eksperimen parameter *Minimum Support* dan *Minimum Confidence* secara interaktif.
