package com.warung.haryati.model;

public class Produk {
    private String idProduk;
    private String namaBarang;
    private double hargaBeli;
    private double hargaJual;
    private int stok;

    public Produk() {}

    public Produk(String idProduk, String namaBarang, double hargaBeli, double hargaJual, int stok) {
        this.idProduk = idProduk;
        this.namaBarang = namaBarang;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
    }

    public String getIdProduk() { return idProduk; }
    public void setIdProduk(String idProduk) { this.idProduk = idProduk; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
}
