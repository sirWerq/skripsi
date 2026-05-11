package com.warung.haryati.controller;

import com.warung.haryati.util.CurrencyUtil;
import com.warung.haryati.util.DBConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class LaporanController {

    @FXML private DatePicker dpStart, dpEnd;
    @FXML private Text txtTotalTransaksi, txtTotalPendapatan;
    @FXML private TableView<LaporanRow> tableLaporan;
    @FXML private TableColumn<LaporanRow, String> colTanggal, colId, colItems, colTotal;

    private ObservableList<LaporanRow> reportData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItems.setCellValueFactory(new PropertyValueFactory<>("items"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalStr"));

        // Default: Current month
        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());

        loadData();
    }

    @FXML
    private void handleFilter() {
        loadData();
    }

    @FXML
    private void handleReset() {
        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());
        loadData();
    }

    private void loadData() {
        reportData.clear();
        double grandTotal = 0;
        int count = 0;

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        if (start == null || end == null) return;

        String sql = "SELECT t.id, t.tanggal, SUM(dt.subtotal) as total_amount, " +
                     "GROUP_CONCAT(CONCAT(p.nama_barang, ' (', dt.kuantitas, ')') SEPARATOR ', ') as items " +
                     "FROM transaksi t " +
                     "JOIN detail_transaksi dt ON t.id = dt.transaksi_id " +
                     "JOIN produk p ON dt.produk_id = p.id " +
                     "WHERE t.tanggal BETWEEN ? AND ? " +
                     "GROUP BY t.id ORDER BY t.tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(start));
            pstmt.setDate(2, Date.valueOf(end));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                reportData.add(new LaporanRow(
                    rs.getDate("tanggal").toString(),
                    rs.getString("id"),
                    rs.getString("items"),
                    total
                ));
                grandTotal += total;
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data laporan: " + e.getMessage());
        }

        tableLaporan.setItems(reportData);
        txtTotalTransaksi.setText(String.valueOf(count));
        txtTotalPendapatan.setText(CurrencyUtil.format(grandTotal));
    }

    @FXML
    private void handleExport() {
        if (reportData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data untuk diexport.");
            return;
        }

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Detail");
        fileChooser.setInitialFileName("Laporan_Detail_Penjualan_" + start + "_sd_" + end + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String sql = "SELECT t.id, t.tanggal, p.nama_barang, p.harga_beli, p.harga_jual, " +
                         "dt.kuantitas, dt.subtotal, dt.laba " +
                         "FROM transaksi t " +
                         "JOIN detail_transaksi dt ON t.id = dt.transaksi_id " +
                         "JOIN produk p ON dt.produk_id = p.id " +
                         "WHERE t.tanggal BETWEEN ? AND ? " +
                         "ORDER BY t.tanggal DESC, t.id ASC";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 FileWriter writer = new FileWriter(file)) {
                 
                pstmt.setDate(1, Date.valueOf(start));
                pstmt.setDate(2, Date.valueOf(end));
                ResultSet rs = pstmt.executeQuery();
                
                writer.write("ID Transaksi;Tanggal;Nama Barang;Harga Beli;Harga Jual;Kuantitas;Subtotal;Laba\n");
                
                int count = 0;
                while (rs.next()) {
                    writer.write(String.format("%s;%s;\"%s\";%.0f;%.0f;%d;%.0f;%.0f\n", 
                        rs.getString("id"),
                        rs.getDate("tanggal").toString(),
                        rs.getString("nama_barang"),
                        rs.getDouble("harga_beli"),
                        rs.getDouble("harga_jual"),
                        rs.getInt("kuantitas"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("laba")));
                    count++;
                }
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan detail berhasil diexport (" + count + " baris).");
            } catch (SQLException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export laporan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class LaporanRow {
        private final SimpleStringProperty tanggal;
        private final SimpleStringProperty id;
        private final SimpleStringProperty items;
        private final SimpleDoubleProperty total;
        private final SimpleStringProperty totalStr;

        public LaporanRow(String tanggal, String id, String items, double total) {
            this.tanggal = new SimpleStringProperty(tanggal);
            this.id = new SimpleStringProperty(id);
            this.items = new SimpleStringProperty(items);
            this.total = new SimpleDoubleProperty(total);
            this.totalStr = new SimpleStringProperty(CurrencyUtil.format(total));
        }

        public String getTanggal() { return tanggal.get(); }
        public String getId() { return id.get(); }
        public String getItems() { return items.get(); }
        public double getTotal() { return total.get(); }
        public String getTotalStr() { return totalStr.get(); }
    }
}
