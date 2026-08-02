package com.warung.haryati.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.model.Produk;
import com.warung.haryati.model.Transaksi;
import com.warung.haryati.util.IDGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public class CSVService {

    private ProdukDao produkDao = new ProdukDao();
    private TransaksiDao transaksiDao = new TransaksiDao();

    public void importFromCSV(File file) throws IOException, SQLException {
        CsvMapper mapper = new CsvMapper();
        char[] separators = {'\t', ';', ','};
        List<Map<String, String>> allRows = null;
        
        for (char sep : separators) {
            try {
                CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(sep);
                MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class).with(schema).readValues(file);
                List<Map<String, String>> rows = it.readAll();
                
                if (!rows.isEmpty() && rows.get(0).containsKey("ID Transaksi")) {
                    allRows = rows;
                    System.out.println("Berhasil mendeteksi separator: " + (sep == '\t' ? "TAB" : sep));
                    break;
                }
            } catch (Exception e) {
            }
        }

        if (allRows == null) {
            throw new IOException("Gagal mendeteksi format CSV. Pastikan terdapat kolom 'ID Transaksi'.");
        }
        
        Map<String, List<Map<String, String>>> groupedTransactions = new LinkedHashMap<>();
        for (Map<String, String> row : allRows) {
            String tId = row.get("ID Transaksi");
            if (tId != null) {
                groupedTransactions.computeIfAbsent(tId.trim(), k -> new ArrayList<>()).add(row);
            }
        }
        
        System.out.println("Ditemukan " + groupedTransactions.size() + " transaksi unik.");

        for (Map.Entry<String, List<Map<String, String>>> entry : groupedTransactions.entrySet()) {
            List<Map<String, String>> rows = entry.getValue();
            String tanggalStr = rows.get(0).get("Tanggal");
            if (tanggalStr == null || tanggalStr.trim().isEmpty()) continue;
            
            Transaksi t = new Transaksi();
            t.setTransaksiId(entry.getKey());
            try {
                String cleanTgl = tanggalStr.trim();
                if (cleanTgl.contains("/")) {
                    String[] parts = cleanTgl.split("/");
                    if (parts.length == 3) {
                        if (parts[2].length() == 4) {
                            cleanTgl = parts[2] + "-" + parts[1] + "-" + parts[0];
                        } else if (parts[0].length() == 4) {
                            cleanTgl = parts[0] + "-" + parts[1] + "-" + parts[2];
                        }
                    }
                }
                t.setTanggal(Date.valueOf(cleanTgl));
            } catch (IllegalArgumentException e) {
                System.err.println("Gagal parsing tanggal: [" + tanggalStr + "]. Gunakan format YYYY-MM-DD atau DD/MM/YYYY.");
                throw new IOException("Format tanggal salah pada: " + tanggalStr);
            }

            
            List<DetailTransaksi> details = new ArrayList<>();
            
            for (Map<String, String> row : rows) {
                String namaBarang = row.get("Nama Barang");
                double hBeli = Double.parseDouble(row.get("Harga Beli"));
                double hJual = Double.parseDouble(row.get("Harga Jual"));
                int qty = Integer.parseInt(row.get("Kuantitas"));
                double subtotal = Double.parseDouble(row.get("Subtotal"));
                double laba = Double.parseDouble(row.get("Laba"));
                int stok = 0;
                if (row.containsKey("Stok") && row.get("Stok") != null && !row.get("Stok").trim().isEmpty()) {
                    stok = Integer.parseInt(row.get("Stok").trim());
                }
                
                Produk p = produkDao.getByNama(namaBarang);
                if (p == null) {
                    p = new Produk();
                    p.setIdProduk(IDGenerator.generate("P"));
                    p.setNamaBarang(namaBarang);
                    p.setHargaBeli(hBeli);
                    p.setHargaJual(hJual);
                    p.setStok(stok);
                    produkDao.insert(p);
                }
                
                DetailTransaksi dt = new DetailTransaksi();
                dt.setDetailId(IDGenerator.generate("DT"));
                dt.setTransaksiId(t.getTransaksiId());
                dt.setProdukId(p.getIdProduk());
                dt.setKuantitas(qty);
                dt.setSubtotal(subtotal);
                dt.setLaba(laba);
                details.add(dt);
            }
            
            try {
                transaksiDao.insertWithDetails(t, details);
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    System.out.println("Melewati transaksi duplikat: " + t.getTransaksiId());
                } else {
                    throw e;
                }
            }
        }
    }
}
