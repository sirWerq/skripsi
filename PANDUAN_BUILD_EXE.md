# Panduan Build Aplikasi ke Executable (.exe)

Dokumen ini berisi panduan langkah demi langkah (manual book) untuk mem-build kode sumber (Java & Python) menjadi sebuah aplikasi desktop standalone berbentuk `.exe` yang dapat langsung dijalankan (Portable / App Image).

---

## Prasyarat (Prerequisites)
Pastikan komputer Anda sudah terinstal perangkat lunak berikut:
1. **JDK (Java Development Kit)** (versi 17 ke atas, direkomendasikan versi 25 sesuai project).
2. **Apache Maven** (terpasang dan dikenali di `Path` / *Environment Variables*).
3. **Python** (versi 3.8 ke atas).

---

## Langkah 1: Build Executable Mesin Analisis Python (`fp_growth.exe`)

Aplikasi ini menggunakan Python sebagai mesin di balik layar untuk melakukan analisis data mining FP-Growth. Kita harus meng-compile file Python tersebut menjadi `.exe` mandiri.

1. Buka Terminal / PowerShell.
2. Masuk ke direktori `python`:
   ```powershell
   cd python
   ```
3. Aktifkan *Virtual Environment* (jika belum aktif):
   ```powershell
   .\venv\Scripts\Activate.ps1
   ```
4. Install semua pustaka yang dibutuhkan (Pastikan Anda menggunakan `pymysql` agar kompatibel dengan PyInstaller):
   ```powershell
   pip install -r requirements.txt
   pip install pyinstaller
   ```
5. Compile `fp_growth.py` menjadi satu file `.exe`:
   ```powershell
   pyinstaller --onefile fp_growth.py
   ```
6. Tunggu hingga selesai. File `fp_growth.exe` yang sudah jadi akan berada di dalam folder `python\dist\`.

---

## Langkah 2: Build Aplikasi Java (Membuat Fat JAR)

Aplikasi GUI JavaFX Anda harus disatukan beserta seluruh library / dependensi (seperti library konektor MySQL, ikon, pembuat PDF) ke dalam satu file JAR utuh yang disebut **Fat JAR**.

1. Buka terminal dan kembali ke direktori utama (root) proyek `skripsi`:
   ```powershell
   cd ..
   ```
2. Bersihkan sisa build sebelumnya dan buat Fat JAR baru menggunakan Maven:
   ```powershell
   mvn clean install
   ```
3. Tunggu hingga muncul tulisan **BUILD SUCCESS**.
4. File Fat JAR Anda sekarang sudah tercipta di dalam folder `target/` dengan nama `skripsi-fp-growth-1.0-SNAPSHOT.jar`.

---

## Langkah 3: Membuat Executable Utama Aplikasi (`.exe`)

Langkah selanjutnya adalah membungkus Fat JAR tersebut ke dalam bentuk executable `.exe` Windows menggunakan tool bawaan JDK bernama `jpackage`.

1. Pastikan file `logo.ico` sudah ada di folder utama `C:\coding\skripsi\` (jika Anda ingin aplikasi memiliki ikon/logo di Windows).
2. Hapus terlebih dahulu folder `output_folder\AplikasiWarungHaryati` (jika sudah pernah di-build sebelumnya) karena `jpackage` akan error jika folder tujuan sudah ada:
   ```powershell
   Remove-Item -Recurse -Force output_folder\AplikasiWarungHaryati -ErrorAction SilentlyContinue
   ```
3. Masih di terminal direktori utama `skripsi`, jalankan perintah berikut untuk mem-build aplikasi beserta logonya:
   ```powershell
   jpackage --type app-image --name AplikasiWarungHaryati --input target/ --main-jar skripsi-fp-growth-1.0-SNAPSHOT.jar --main-class com.warung.haryati.Main --icon logo.ico --dest output_folder
   ```
   > **💡 Tips Tambahan:** 
   > - Gunakan `--type app-image` untuk menghasilkan aplikasi portabel berupa folder. Jika ingin membuat file Installer Setup (`.msi` atau `.exe` installer), Anda bisa menggantinya dengan `--type exe` atau `--type msi` (Membutuhkan *WiX Toolset* terinstal di PC).
   > - Jika Anda ingin command prompt (layar hitam) muncul di belakang aplikasi untuk melihat log/error, tambahkan flag `--win-console`. Jika aplikasi sudah siap rilis, hilangkan flag tersebut.

2. Tunggu proses selesai. Aplikasi `.exe` Anda kini telah terbuat di dalam folder `output_folder\AplikasiWarungHaryati\`.

---

## Langkah 4: Menggabungkan Executable Python

Agar aplikasi Java dapat memanggil mesin Python saat tombol "Analisis" ditekan, `fp_growth.exe` harus berada di direktori yang sama dengan aplikasi Java.

1. Salin (Copy) file `fp_growth.exe` dari folder `python\dist\`.
2. Tempel (Paste) ke dalam folder `output_folder\AplikasiWarungHaryati\`.
3. Anda bisa melakukannya secara manual lewat File Explorer atau menggunakan perintah ini:
   ```powershell
   Copy-Item python\dist\fp_growth.exe -Destination output_folder\AplikasiWarungHaryati\ -Force
   ```

---

## Langkah 5: Selesai! 🎉

Proses build selesai. Sekarang, Anda dapat menyalin seluruh folder `output_folder\AplikasiWarungHaryati` tersebut ke Flashdisk atau komputer kasir lain, dan aplikasi dapat dijalankan cukup dengan klik dua kali pada **AplikasiWarungHaryati.exe**.
