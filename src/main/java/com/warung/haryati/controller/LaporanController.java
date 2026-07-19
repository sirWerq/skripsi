package com.warung.haryati.controller;

import com.warung.haryati.model.AnalisisResult;
import com.warung.haryati.service.FPGrowthService;
import com.warung.haryati.util.CurrencyUtil;
import com.warung.haryati.util.DBConnection;
import com.warung.haryati.util.PdfReportUtil;
import com.itextpdf.text.DocumentException;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class LaporanController {

    @FXML private DatePicker dpStart, dpEnd;
    @FXML private TabPane tabPaneLaporan;

    @FXML private Text txtTotalTransaksiLaba, txtTotalOmsetLaba, txtTotalModalLaba, txtTotalLabaBersih;
    @FXML private TableView<LabaRugiRow> tableLabaRugi;
    @FXML private TableColumn<LabaRugiRow, String> colLabaTanggal, colLabaJmlTransaksi, colLabaOmset, colLabaModal, colLabaBersih;
    private ObservableList<LabaRugiRow> labaRugiData = FXCollections.observableArrayList();

    @FXML private TableView<BarangTerlarisRow> tableBarangTerlaris;
    @FXML private TableColumn<BarangTerlarisRow, String> colRank, colNamaBarang, colJmlTerjual, colOmsetBarang, colLabaBarang;
    private ObservableList<BarangTerlarisRow> barangTerlarisData = FXCollections.observableArrayList();

    @FXML private TableView<FpGrowthReportRow> tableFpGrowthReport;
    @FXML private TableColumn<FpGrowthReportRow, String> colRuleAntecedents, colRuleConsequents, colRuleSupport, colRuleConfidence, colRuleLift, colRuleRekomendasi;
    private ObservableList<FpGrowthReportRow> fpGrowthReportData = FXCollections.observableArrayList();

    @FXML private Text txtTotalTransaksi, txtTotalPendapatan;
    @FXML private TableView<LaporanRow> tableLaporan;
    @FXML private TableColumn<LaporanRow, String> colTanggal, colId, colItems, colTotal;
    private ObservableList<LaporanRow> reportData = FXCollections.observableArrayList();

    @FXML private TableView<DataProdukRow> tableDataProduk;
    @FXML private TableColumn<DataProdukRow, String> colProdukNo, colProdukId, colProdukNama, colProdukHargaBeli, colProdukHargaJual, colProdukStok;
    @FXML private Text txtTotalProduk;
    private ObservableList<DataProdukRow> dataProdukList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupDatePickers();
        setupTables();
        setDefaultDateRange();
        loadAllReports();
        checkAndLoadExistingFpGrowthResult();
        
        if (tabPaneLaporan != null) {
            tabPaneLaporan.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null && "🧠 Analisis FP-Growth".equals(newTab.getText())) {
                    checkAndLoadExistingFpGrowthResult();
                }
            });
        }
    }

    private void setupDatePickers() {
        javafx.util.StringConverter<LocalDate> converter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? com.warung.haryati.util.DateUtil.formatShort(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                return parseFlexibleDate(string);
            }
        };
        if (dpStart != null) dpStart.setConverter(converter);
        if (dpEnd != null) dpEnd.setConverter(converter);
    }

    private void setDefaultDateRange() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MIN(DATE(tanggal)), MAX(DATE(tanggal)) FROM transaksi")) {
            if (rs.next() && rs.getDate(1) != null) {
                dpStart.setValue(rs.getDate(1).toLocalDate());
                dpEnd.setValue(rs.getDate(2).toLocalDate());
            } else {
                dpStart.setValue(LocalDate.now().withDayOfMonth(1));
                dpEnd.setValue(LocalDate.now());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            dpStart.setValue(LocalDate.now().withDayOfMonth(1));
            dpEnd.setValue(LocalDate.now());
        }
    }

    private void setupTables() {
        colLabaTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colLabaJmlTransaksi.setCellValueFactory(new PropertyValueFactory<>("jmlTransaksiStr"));
        colLabaOmset.setCellValueFactory(new PropertyValueFactory<>("omsetStr"));
        colLabaModal.setCellValueFactory(new PropertyValueFactory<>("modalStr"));
        colLabaBersih.setCellValueFactory(new PropertyValueFactory<>("labaStr"));

        colRank.setCellValueFactory(new PropertyValueFactory<>("rankStr"));
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJmlTerjual.setCellValueFactory(new PropertyValueFactory<>("jmlTerjualStr"));
        colOmsetBarang.setCellValueFactory(new PropertyValueFactory<>("omsetStr"));
        colLabaBarang.setCellValueFactory(new PropertyValueFactory<>("labaStr"));

        colRuleAntecedents.setCellValueFactory(new PropertyValueFactory<>("antecedents"));
        colRuleConsequents.setCellValueFactory(new PropertyValueFactory<>("consequents"));
        colRuleSupport.setCellValueFactory(new PropertyValueFactory<>("supportStr"));
        colRuleConfidence.setCellValueFactory(new PropertyValueFactory<>("confidenceStr"));
        colRuleLift.setCellValueFactory(new PropertyValueFactory<>("liftStr"));
        colRuleRekomendasi.setCellValueFactory(new PropertyValueFactory<>("rekomendasi"));

        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItems.setCellValueFactory(new PropertyValueFactory<>("items"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalStr"));

        colProdukNo.setCellValueFactory(new PropertyValueFactory<>("noStr"));
        colProdukId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdukNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colProdukHargaBeli.setCellValueFactory(new PropertyValueFactory<>("hargaBeliStr"));
        colProdukHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJualStr"));
        colProdukStok.setCellValueFactory(new PropertyValueFactory<>("stokStr"));

        for (TableView<?> table : new TableView<?>[]{tableLabaRugi, tableBarangTerlaris, tableDataProduk, tableFpGrowthReport, tableLaporan}) {
            if (table != null) {
                for (TableColumn<?, ?> col : table.getColumns()) {
                    col.setSortable(false);
                }
            }
        }
    }

    @FXML
    private void handleFilter() {
        loadAllReports();
    }

    @FXML
    private void handleReset() {
        setDefaultDateRange();
        loadAllReports();
    }

    private void commitDatePickers() {
        try {
            if (dpStart != null && dpStart.getEditor() != null && !dpStart.getEditor().getText().trim().isEmpty()) {
                LocalDate parsed = parseFlexibleDate(dpStart.getEditor().getText().trim());
                if (parsed != null) dpStart.setValue(parsed);
            }
            if (dpEnd != null && dpEnd.getEditor() != null && !dpEnd.getEditor().getText().trim().isEmpty()) {
                LocalDate parsed = parseFlexibleDate(dpEnd.getEditor().getText().trim());
                if (parsed != null) dpEnd.setValue(parsed);
            }
        } catch (Exception e) {
            System.out.println("Gagal commit format tanggal manual: " + e.getMessage());
        }
    }

    private LocalDate parseFlexibleDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String clean = text.trim();
        try {
            return LocalDate.parse(clean, com.warung.haryati.util.DateUtil.FORMAT_SHORT);
        } catch (Exception ignored) {}
        
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "M/d/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd", "dd/M/yyyy", "d/M/yyyy"};
        for (String fmt : formats) {
            try {
                return LocalDate.parse(clean, java.time.format.DateTimeFormatter.ofPattern(fmt));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void loadAllReports() {
        commitDatePickers();
        System.out.println("=== FILTERING REPORT ===");
        System.out.println("Start Date: " + (dpStart != null ? dpStart.getValue() : "null"));
        System.out.println("End Date: " + (dpEnd != null ? dpEnd.getValue() : "null"));
        loadLabaRugi();
        loadBarangTerlaris();
        loadDetailTransaksi();
        loadDataProduk();
        checkAndLoadExistingFpGrowthResult();
    }

    private void loadLabaRugi() {
        labaRugiData.clear();
        int totalTx = 0;
        double grandOmset = 0;
        double grandModal = 0;
        double grandLaba = 0;

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();
        if (start == null || end == null) return;

        String sql = "SELECT DATE(t.tanggal) as tanggal, COUNT(DISTINCT t.id) as jml_transaksi, " +
                     "SUM(dt.subtotal) as omset, " +
                     "SUM(dt.kuantitas * p.harga_beli) as modal, " +
                     "SUM(dt.laba) as laba_bersih " +
                     "FROM transaksi t " +
                     "JOIN detail_transaksi dt ON t.id = dt.transaksi_id " +
                     "JOIN produk p ON dt.produk_id = p.id " +
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "GROUP BY DATE(t.tanggal) ORDER BY tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(start));
            pstmt.setDate(2, Date.valueOf(end));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String tgl = com.warung.haryati.util.DateUtil.formatShort(rs.getString("tanggal"));
                int jmlTx = rs.getInt("jml_transaksi");
                double omset = rs.getDouble("omset");
                double modal = rs.getDouble("modal");
                double laba = rs.getDouble("laba_bersih");
                
                labaRugiData.add(new LabaRugiRow(tgl, jmlTx, omset, modal, laba));
                totalTx += jmlTx;
                grandOmset += omset;
                grandModal += modal;
                grandLaba += laba;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat Laporan Laba Rugi: " + e.getMessage());
        }

        tableLabaRugi.setItems(labaRugiData);
        tableLabaRugi.refresh();
        txtTotalTransaksiLaba.setText(String.valueOf(totalTx));
        txtTotalOmsetLaba.setText(CurrencyUtil.format(grandOmset));
        txtTotalModalLaba.setText(CurrencyUtil.format(grandModal));
        txtTotalLabaBersih.setText(CurrencyUtil.format(grandLaba));
    }

    @FXML
    private void handleExportLabaRugi() {
        commitDatePickers();
        if (labaRugiData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data Laba Rugi untuk diexport.");
            return;
        }

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Laba Rugi (PDF)");
        fileChooser.setInitialFileName("Laporan_Laba_Rugi_" + start + "_sd_" + end + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PdfReportUtil.exportLabaRugi(file, labaRugiData, start, end);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan Laba Rugi berhasil diexport ke format PDF dengan KOP Surat dan Tanda Tangan rapi (" + labaRugiData.size() + " baris).");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export Laporan Laba Rugi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadBarangTerlaris() {
        barangTerlarisData.clear();
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();
        if (start == null || end == null) return;

        String sql = "SELECT p.nama_barang, SUM(dt.kuantitas) as total_terjual, " +
                     "SUM(dt.subtotal) as total_pendapatan, SUM(dt.laba) as total_laba " +
                     "FROM detail_transaksi dt " +
                     "JOIN produk p ON dt.produk_id = p.id " +
                     "JOIN transaksi t ON dt.transaksi_id = t.id " +
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "GROUP BY p.id, p.nama_barang " +
                     "ORDER BY total_terjual DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(start));
            pstmt.setDate(2, Date.valueOf(end));
            ResultSet rs = pstmt.executeQuery();
            
            int rank = 1;
            while (rs.next()) {
                barangTerlarisData.add(new BarangTerlarisRow(
                    rank++,
                    rs.getString("nama_barang"),
                    rs.getInt("total_terjual"),
                    rs.getDouble("total_pendapatan"),
                    rs.getDouble("total_laba")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat Laporan Barang Terlaris: " + e.getMessage());
        }

        tableBarangTerlaris.setItems(barangTerlarisData);
        tableBarangTerlaris.refresh();
    }

    @FXML
    private void handleExportBarangTerlaris() {
        commitDatePickers();
        if (barangTerlarisData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data Barang Terlaris untuk diexport.");
            return;
        }

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Barang Terlaris (PDF)");
        fileChooser.setInitialFileName("Laporan_Barang_Terlaris_" + start + "_sd_" + end + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PdfReportUtil.exportBarangTerlaris(file, barangTerlarisData, start, end);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan Barang Terlaris berhasil diexport ke format PDF dengan KOP Surat dan Tanda Tangan rapi (" + barangTerlarisData.size() + " baris).");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export Laporan Barang Terlaris: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void checkAndLoadExistingFpGrowthResult() {
        AnalisisResult result = FPGrowthService.getLatestResult();
        fpGrowthReportData.clear();
        if (result != null && result.getAssociation_rules() != null && !result.getAssociation_rules().isEmpty()) {
            for (AnalisisResult.AssociationRule rule : result.getAssociation_rules()) {
                String rek;
                if (rule.getLift() > 1.0) {
                    rek = String.format("Bundling / dekatkan posisi produk [%s] dengan [%s] (Peluang beli bersamaan %.0f%%)",
                        rule.getAntecedentsString(), rule.getConsequentsString(), rule.getConfidence() * 100);
                } else {
                    rek = String.format("Buat promo diskon bersyarat untuk [%s] setiap pembelian [%s]",
                        rule.getConsequentsString(), rule.getAntecedentsString());
                }
                fpGrowthReportData.add(new FpGrowthReportRow(
                    rule.getAntecedentsString(),
                    rule.getConsequentsString(),
                    rule.getSupport(),
                    rule.getConfidence(),
                    rule.getLift(),
                    rek
                ));
            }
            tableFpGrowthReport.setItems(fpGrowthReportData);
        } else {
            tableFpGrowthReport.setPlaceholder(new Label("Belum ada laporan analisis FP-Growth.\nSilakan jalankan analisis terlebih dahulu di menu Analisis FP-Growth."));
        }
    }

    @FXML
    private void handleExportFpGrowth() {
        if (fpGrowthReportData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data Analisis FP-Growth untuk diexport. Silakan jalankan analisis terlebih dahulu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Analisis FP-Growth (PDF)");
        fileChooser.setInitialFileName("Laporan_Analisis_FPGrowth_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PdfReportUtil.exportFpGrowth(file, fpGrowthReportData, null, null);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan Analisis FP-Growth berhasil diexport ke format PDF dengan KOP Surat dan Tanda Tangan rapi (" + fpGrowthReportData.size() + " baris).");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export Laporan FP-Growth: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadDetailTransaksi() {
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
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "GROUP BY t.id ORDER BY t.tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(start));
            pstmt.setDate(2, Date.valueOf(end));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                reportData.add(new LaporanRow(
                    com.warung.haryati.util.DateUtil.formatShort(rs.getDate("tanggal")),
                    rs.getString("id"),
                    rs.getString("items"),
                    total
                ));
                grandTotal += total;
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data laporan detail transaksi: " + e.getMessage());
        }

        tableLaporan.setItems(reportData);
        tableLaporan.refresh();
        txtTotalTransaksi.setText(String.valueOf(count));
        txtTotalPendapatan.setText(CurrencyUtil.format(grandTotal));
    }

    @FXML
    private void handleExport() {
        commitDatePickers();
        if (reportData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data transaksi untuk diexport.");
            return;
        }

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Detail Penjualan (PDF)");
        fileChooser.setInitialFileName("Laporan_Detail_Penjualan_" + start + "_sd_" + end + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PdfReportUtil.exportDetailTransaksi(file, reportData, start, end);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan Penjualan berhasil diexport ke format PDF dengan KOP Surat dan Tanda Tangan rapi (" + reportData.size() + " transaksi).");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export laporan detail: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadDataProduk() {
        dataProdukList.clear();
        int totalProduk = 0;

        String sql = "SELECT id, nama_barang, harga_beli, harga_jual, stok FROM produk ORDER BY nama_barang ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int no = 1;
            while (rs.next()) {
                String id = rs.getString("id");
                String nama = rs.getString("nama_barang");
                double beli = rs.getDouble("harga_beli");
                double jual = rs.getDouble("harga_jual");
                int stok = rs.getInt("stok");

                dataProdukList.add(new DataProdukRow(no++, id, nama, beli, jual, stok));
                
                totalProduk++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat Laporan Data Produk: " + e.getMessage());
        }

        tableDataProduk.setItems(dataProdukList);
        tableDataProduk.refresh();
        txtTotalProduk.setText(totalProduk + " Item");
    }

    @FXML
    private void handleExportDataProduk() {
        if (dataProdukList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tidak ada data produk untuk diexport.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Data Produk (PDF)");
        fileChooser.setInitialFileName("Laporan_Data_Produk_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PdfReportUtil.exportDataProduk(file, dataProdukList);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan Data Produk berhasil diexport ke format PDF dengan KOP Surat dan Tanda Tangan rapi (" + dataProdukList.size() + " produk).");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal export Laporan Data Produk: " + e.getMessage());
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

    public static class LabaRugiRow {
        private final SimpleStringProperty tanggal;
        private final SimpleIntegerProperty jmlTransaksi;
        private final SimpleStringProperty jmlTransaksiStr;
        private final SimpleDoubleProperty omset;
        private final SimpleStringProperty omsetStr;
        private final SimpleDoubleProperty modal;
        private final SimpleStringProperty modalStr;
        private final SimpleDoubleProperty laba;
        private final SimpleStringProperty labaStr;

        public LabaRugiRow(String tanggal, int jmlTransaksi, double omset, double modal, double laba) {
            this.tanggal = new SimpleStringProperty(tanggal);
            this.jmlTransaksi = new SimpleIntegerProperty(jmlTransaksi);
            this.jmlTransaksiStr = new SimpleStringProperty(String.valueOf(jmlTransaksi));
            this.omset = new SimpleDoubleProperty(omset);
            this.omsetStr = new SimpleStringProperty(CurrencyUtil.format(omset));
            this.modal = new SimpleDoubleProperty(modal);
            this.modalStr = new SimpleStringProperty(CurrencyUtil.format(modal));
            this.laba = new SimpleDoubleProperty(laba);
            this.labaStr = new SimpleStringProperty(CurrencyUtil.format(laba));
        }

        public String getTanggal() { return tanggal.get(); }
        public int getJmlTransaksi() { return jmlTransaksi.get(); }
        public String getJmlTransaksiStr() { return jmlTransaksiStr.get(); }
        public double getOmset() { return omset.get(); }
        public String getOmsetStr() { return omsetStr.get(); }
        public double getModal() { return modal.get(); }
        public String getModalStr() { return modalStr.get(); }
        public double getLaba() { return laba.get(); }
        public String getLabaStr() { return labaStr.get(); }
    }

    public static class BarangTerlarisRow {
        private final SimpleIntegerProperty rank;
        private final SimpleStringProperty rankStr;
        private final SimpleStringProperty namaBarang;
        private final SimpleIntegerProperty jmlTerjual;
        private final SimpleStringProperty jmlTerjualStr;
        private final SimpleDoubleProperty omset;
        private final SimpleStringProperty omsetStr;
        private final SimpleDoubleProperty laba;
        private final SimpleStringProperty labaStr;

        public BarangTerlarisRow(int rank, String namaBarang, int jmlTerjual, double omset, double laba) {
            this.rank = new SimpleIntegerProperty(rank);
            this.rankStr = new SimpleStringProperty(String.valueOf(rank));
            this.namaBarang = new SimpleStringProperty(namaBarang);
            this.jmlTerjual = new SimpleIntegerProperty(jmlTerjual);
            this.jmlTerjualStr = new SimpleStringProperty(String.valueOf(jmlTerjual));
            this.omset = new SimpleDoubleProperty(omset);
            this.omsetStr = new SimpleStringProperty(CurrencyUtil.format(omset));
            this.laba = new SimpleDoubleProperty(laba);
            this.labaStr = new SimpleStringProperty(CurrencyUtil.format(laba));
        }

        public int getRank() { return rank.get(); }
        public String getRankStr() { return rankStr.get(); }
        public String getNamaBarang() { return namaBarang.get(); }
        public int getJmlTerjual() { return jmlTerjual.get(); }
        public String getJmlTerjualStr() { return jmlTerjualStr.get(); }
        public double getOmset() { return omset.get(); }
        public String getOmsetStr() { return omsetStr.get(); }
        public double getLaba() { return laba.get(); }
        public String getLabaStr() { return labaStr.get(); }
    }

    public static class FpGrowthReportRow {
        private final SimpleStringProperty antecedents;
        private final SimpleStringProperty consequents;
        private final SimpleDoubleProperty support;
        private final SimpleStringProperty supportStr;
        private final SimpleDoubleProperty confidence;
        private final SimpleStringProperty confidenceStr;
        private final SimpleDoubleProperty lift;
        private final SimpleStringProperty liftStr;
        private final SimpleStringProperty rekomendasi;

        public FpGrowthReportRow(String antecedents, String consequents, double support, double confidence, double lift, String rekomendasi) {
            this.antecedents = new SimpleStringProperty(antecedents);
            this.consequents = new SimpleStringProperty(consequents);
            this.support = new SimpleDoubleProperty(support);
            this.supportStr = new SimpleStringProperty(String.format("%.3f", support));
            this.confidence = new SimpleDoubleProperty(confidence);
            this.confidenceStr = new SimpleStringProperty(String.format("%.3f", confidence));
            this.lift = new SimpleDoubleProperty(lift);
            this.liftStr = new SimpleStringProperty(String.format("%.3f", lift));
            this.rekomendasi = new SimpleStringProperty(rekomendasi);
        }

        public String getAntecedents() { return antecedents.get(); }
        public String getConsequents() { return consequents.get(); }
        public double getSupport() { return support.get(); }
        public String getSupportStr() { return supportStr.get(); }
        public double getConfidence() { return confidence.get(); }
        public String getConfidenceStr() { return confidenceStr.get(); }
        public double getLift() { return lift.get(); }
        public String getLiftStr() { return liftStr.get(); }
        public String getRekomendasi() { return rekomendasi.get(); }
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

    public static class DataProdukRow {
        private final SimpleIntegerProperty no;
        private final SimpleStringProperty noStr;
        private final SimpleStringProperty id;
        private final SimpleStringProperty namaBarang;
        private final SimpleDoubleProperty hargaBeli;
        private final SimpleStringProperty hargaBeliStr;
        private final SimpleDoubleProperty hargaJual;
        private final SimpleStringProperty hargaJualStr;
        private final SimpleIntegerProperty stok;
        private final SimpleStringProperty stokStr;

        public DataProdukRow(int no, String id, String namaBarang, double hargaBeli, double hargaJual, int stok) {
            this.no = new SimpleIntegerProperty(no);
            this.noStr = new SimpleStringProperty(String.valueOf(no));
            this.id = new SimpleStringProperty(id);
            this.namaBarang = new SimpleStringProperty(namaBarang);
            this.hargaBeli = new SimpleDoubleProperty(hargaBeli);
            this.hargaBeliStr = new SimpleStringProperty(CurrencyUtil.format(hargaBeli));
            this.hargaJual = new SimpleDoubleProperty(hargaJual);
            this.hargaJualStr = new SimpleStringProperty(CurrencyUtil.format(hargaJual));
            this.stok = new SimpleIntegerProperty(stok);
            this.stokStr = new SimpleStringProperty(String.valueOf(stok));
        }

        public int getNo() { return no.get(); }
        public String getNoStr() { return noStr.get(); }
        public String getId() { return id.get(); }
        public String getNamaBarang() { return namaBarang.get(); }
        public double getHargaBeli() { return hargaBeli.get(); }
        public String getHargaBeliStr() { return hargaBeliStr.get(); }
        public double getHargaJual() { return hargaJual.get(); }
        public String getHargaJualStr() { return hargaJualStr.get(); }
        public int getStok() { return stok.get(); }
        public String getStokStr() { return stokStr.get(); }
    }
}
