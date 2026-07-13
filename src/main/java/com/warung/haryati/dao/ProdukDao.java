package com.warung.haryati.dao;

import com.warung.haryati.model.Produk;
import com.warung.haryati.util.DBConnection;
import com.warung.haryati.util.IDGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDao {

    public List<Produk> getAll() throws SQLException {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Produk(
                    rs.getString("id"),
                    rs.getString("nama_barang"),
                    rs.getDouble("harga_beli"),
                    rs.getDouble("harga_jual")
                ));
            }
        }
        return list;
    }

    public void insert(Produk p) throws SQLException {
        if (p.getId() == null) p.setId(IDGenerator.generate("P"));
        String sql = "INSERT INTO produk (id, nama_barang, harga_beli, harga_jual) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getId());
            pstmt.setString(2, p.getNamaBarang());
            pstmt.setDouble(3, p.getHargaBeli());
            pstmt.setDouble(4, p.getHargaJual());
            pstmt.executeUpdate();
        }
    }

    public void update(Produk p) throws SQLException {
        String sql = "UPDATE produk SET nama_barang=?, harga_beli=?, harga_jual=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNamaBarang());
            pstmt.setDouble(2, p.getHargaBeli());
            pstmt.setDouble(3, p.getHargaJual());
            pstmt.setString(4, p.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        if (isUsedInTransaction(id)) {
            throw new SQLException("Produk tidak bisa dihapus karena sudah ada dalam data transaksi.");
        }

        String sql = "DELETE FROM produk WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    private boolean isUsedInTransaction(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM detail_transaksi WHERE produk_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public Produk getByNama(String nama) throws SQLException {
        String sql = "SELECT * FROM produk WHERE nama_barang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Produk(
                    rs.getString("id"),
                    rs.getString("nama_barang"),
                    rs.getDouble("harga_beli"),
                    rs.getDouble("harga_jual")
                );
            }
        }
        return null;
    }
}
