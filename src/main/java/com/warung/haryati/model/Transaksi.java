package com.warung.haryati.model;

import java.sql.Date;

public class Transaksi {
    private String id;
    private Date tanggal;
    private double total;

    public Transaksi() {}

    public Transaksi(String id, Date tanggal) {
        this.id = id;
        this.tanggal = tanggal;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
