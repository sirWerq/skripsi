package com.warung.haryati.model;

public class Produk {
    private String id;
    private String namaBarang;
    private double hargaBeli;
    private double hargaJual;
    private int stok;

    public Produk() {}

    public Produk(String id, String namaBarang, double hargaBeli, double hargaJual, int stok) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
}
