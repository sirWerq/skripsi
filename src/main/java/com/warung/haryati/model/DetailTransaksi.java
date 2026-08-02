package com.warung.haryati.model;

public class DetailTransaksi {
    private String detailId;
    private String transaksiId;
    private String produkId;
    private int kuantitas;
    private double subtotal;
    private double laba;

    public DetailTransaksi() {}

    public String getDetailId() { return detailId; }
    public void setDetailId(String detailId) { this.detailId = detailId; }

    public String getTransaksiId() { return transaksiId; }
    public void setTransaksiId(String transaksiId) { this.transaksiId = transaksiId; }

    public String getProdukId() { return produkId; }
    public void setProdukId(String produkId) { this.produkId = produkId; }

    public int getKuantitas() { return kuantitas; }
    public void setKuantitas(int kuantitas) { this.kuantitas = kuantitas; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getLaba() { return laba; }
    public void setLaba(double laba) { this.laba = laba; }
}
