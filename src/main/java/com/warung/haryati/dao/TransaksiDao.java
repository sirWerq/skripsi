package com.warung.haryati.dao;

import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.model.Transaksi;
import com.warung.haryati.util.DBConnection;
import com.warung.haryati.util.IDGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDao {

    public void insertWithDetails(Transaksi t, List<DetailTransaksi> details) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            if (t.getId() == null) t.setId(IDGenerator.generate("TRX"));

            // Insert Transaksi
            String sqlT = "INSERT INTO transaksi (id, tanggal) VALUES (?, ?)";
            PreparedStatement pstmtT = conn.prepareStatement(sqlT);
            pstmtT.setString(1, t.getId());
            pstmtT.setDate(2, t.getTanggal());
            pstmtT.executeUpdate();

            // Insert Details
            String sqlD = "INSERT INTO detail_transaksi (id, transaksi_id, produk_id, kuantitas, subtotal, laba) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            for (DetailTransaksi d : details) {
                if (d.getId() == null) d.setId(IDGenerator.generate("DT"));
                pstmtD.setString(1, d.getId());
                pstmtD.setString(2, t.getId());
                pstmtD.setString(3, d.getProdukId());
                pstmtD.setInt(4, d.getKuantitas());
                pstmtD.setDouble(5, d.getSubtotal());
                pstmtD.setDouble(6, d.getLaba());
                pstmtD.addBatch();
            }
            pstmtD.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<Transaksi> getAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Transaksi(rs.getString("id"), rs.getDate("tanggal")));
            }
        }
        return list;
    }

    public List<DetailTransaksi> getDetailsByTransaksiId(String tId) throws SQLException {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE transaksi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setId(rs.getString("id"));
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

    public double getTotalByTransaksiId(String tId) throws SQLException {
        String sql = "SELECT SUM(subtotal) FROM detail_transaksi WHERE transaksi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public void delete(String tId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            String sqlD = "DELETE FROM detail_transaksi WHERE transaksi_id = ?";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            pstmtD.setString(1, tId);
            pstmtD.executeUpdate();
            
            String sqlT = "DELETE FROM transaksi WHERE id = ?";
            PreparedStatement pstmtT = conn.prepareStatement(sqlT);
            pstmtT.setString(1, tId);
            pstmtT.executeUpdate();
            
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    public void updateWithDetails(Transaksi t, List<DetailTransaksi> details) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // Delete old details
            String sqlD = "DELETE FROM detail_transaksi WHERE transaksi_id = ?";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            pstmtD.setString(1, t.getId());
            pstmtD.executeUpdate();
            
            // Update transaction date
            String sqlT = "UPDATE transaksi SET tanggal = ? WHERE id = ?";
            PreparedStatement pstmtT = conn.prepareStatement(sqlT);
            pstmtT.setDate(1, t.getTanggal());
            pstmtT.setString(2, t.getId());
            pstmtT.executeUpdate();
            
            // Insert new details
            String sqlDI = "INSERT INTO detail_transaksi (id, transaksi_id, produk_id, kuantitas, subtotal, laba) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtDI = conn.prepareStatement(sqlDI);
            for (DetailTransaksi d : details) {
                if (d.getId() == null) d.setId(IDGenerator.generate("DT"));
                pstmtDI.setString(1, d.getId());
                pstmtDI.setString(2, t.getId());
                pstmtDI.setString(3, d.getProdukId());
                pstmtDI.setInt(4, d.getKuantitas());
                pstmtDI.setDouble(5, d.getSubtotal());
                pstmtDI.setDouble(6, d.getLaba());
                pstmtDI.addBatch();
            }
            pstmtDI.executeBatch();
            
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
