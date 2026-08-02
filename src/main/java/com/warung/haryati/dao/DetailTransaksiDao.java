package com.warung.haryati.dao;

import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.util.DBConnection;
import com.warung.haryati.util.IDGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiDao {

    public List<DetailTransaksi> getAll() throws SQLException {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi ORDER BY detail_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setDetailId(rs.getString("detail_id"));
                d.setTransaksiId(rs.getString("transaksi_id"));
                d.setProdukId(rs.getString("produk_id"));
                d.setKuantitas(rs.getInt("kuantitas"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setLaba(rs.getDouble("laba"));
                list.add(d);
            }
        }
        return list;
    }

    public void insert(DetailTransaksi d) throws SQLException {
        if (d.getDetailId() == null) d.setDetailId(IDGenerator.generate("DT"));
        String sql = "INSERT INTO detail_transaksi (detail_id, transaksi_id, produk_id, kuantitas, subtotal, laba) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getDetailId());
            pstmt.setString(2, d.getTransaksiId());
            pstmt.setString(3, d.getProdukId());
            pstmt.setInt(4, d.getKuantitas());
            pstmt.setDouble(5, d.getSubtotal());
            pstmt.setDouble(6, d.getLaba());
            pstmt.executeUpdate();
        }
    }

    public void update(DetailTransaksi d) throws SQLException {
        String sql = "UPDATE detail_transaksi SET transaksi_id=?, produk_id=?, kuantitas=?, subtotal=?, laba=? WHERE detail_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getTransaksiId());
            pstmt.setString(2, d.getProdukId());
            pstmt.setInt(3, d.getKuantitas());
            pstmt.setDouble(4, d.getSubtotal());
            pstmt.setDouble(5, d.getLaba());
            pstmt.setString(6, d.getDetailId());
            pstmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM detail_transaksi WHERE detail_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
}
