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

            if (t.getTransaksiId() == null) t.setTransaksiId(IDGenerator.generate("TRX"));

            String sqlT = "INSERT INTO transaksi (transaksi_id, tanggal, total_belanja) VALUES (?, ?, ?)";
            PreparedStatement pstmtT = conn.prepareStatement(sqlT);
            pstmtT.setString(1, t.getTransaksiId());
            pstmtT.setDate(2, t.getTanggal());
            pstmtT.setDouble(3, t.getTotalBelanja());
            pstmtT.executeUpdate();

            String sqlD = "INSERT INTO detail_transaksi (detail_id, transaksi_id, produk_id, kuantitas, subtotal, laba) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            for (DetailTransaksi d : details) {
                if (d.getDetailId() == null) d.setDetailId(IDGenerator.generate("DT"));
                pstmtD.setString(1, d.getDetailId());
                pstmtD.setString(2, t.getTransaksiId());
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

    public void insert(Transaksi t) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO transaksi (transaksi_id, tanggal, total_belanja) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getTransaksiId());
            pstmt.setDate(2, t.getTanggal());
            pstmt.setDouble(3, t.getTotalBelanja());
            pstmt.executeUpdate();
        }
    }

    public void update(Transaksi t) throws SQLException {
        String sql = "UPDATE transaksi SET tanggal = ?, total_belanja = ? WHERE transaksi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, t.getTanggal());
            pstmt.setDouble(2, t.getTotalBelanja());
            pstmt.setString(3, t.getTransaksiId());
            pstmt.executeUpdate();
        }
    }

    public List<Transaksi> getAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi tx = new Transaksi(rs.getString("transaksi_id"), rs.getDate("tanggal"));
                tx.setTotalBelanja(rs.getDouble("total_belanja"));
                list.add(tx);
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
        // Return stock before deleting details
        List<DetailTransaksi> details = getDetailsByTransaksiId(tId);
        ProdukDao produkDao = new ProdukDao();
        for (DetailTransaksi d : details) {
            produkDao.updateStok(d.getProdukId(), d.getKuantitas());
        }

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            String sqlD = "DELETE FROM detail_transaksi WHERE transaksi_id = ?";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            pstmtD.setString(1, tId);
            pstmtD.executeUpdate();
            
            String sqlT = "DELETE FROM transaksi WHERE transaksi_id = ?";
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
            
            String sqlD = "DELETE FROM detail_transaksi WHERE transaksi_id = ?";
            PreparedStatement pstmtD = conn.prepareStatement(sqlD);
            pstmtD.setString(1, t.getTransaksiId());
            pstmtD.executeUpdate();
            
            String sqlT = "UPDATE transaksi SET tanggal = ?, total_belanja = ? WHERE transaksi_id = ?";
            PreparedStatement pstmtT = conn.prepareStatement(sqlT);
            pstmtT.setDate(1, t.getTanggal());
            pstmtT.setDouble(2, t.getTotalBelanja());
            pstmtT.setString(3, t.getTransaksiId());
            pstmtT.executeUpdate();
            
            String sqlDI = "INSERT INTO detail_transaksi (detail_id, transaksi_id, produk_id, kuantitas, subtotal, laba) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtDI = conn.prepareStatement(sqlDI);
            for (DetailTransaksi d : details) {
                if (d.getDetailId() == null) d.setDetailId(IDGenerator.generate("DT"));
                pstmtDI.setString(1, d.getDetailId());
                pstmtDI.setString(2, t.getTransaksiId());
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
