package com.warung.haryati.model;

import java.sql.Date;

public class Transaksi {
    private String transaksiId;
    private Date tanggal;
    private double totalBelanja;

    public Transaksi() {}

    public Transaksi(String transaksiId, Date tanggal) {
        this.transaksiId = transaksiId;
        this.tanggal = tanggal;
    }

    public String getTransaksiId() { return transaksiId; }
    public void setTransaksiId(String transaksiId) { this.transaksiId = transaksiId; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public double getTotalBelanja() { return totalBelanja; }
    public void setTotalBelanja(double totalBelanja) { this.totalBelanja = totalBelanja; }
}
