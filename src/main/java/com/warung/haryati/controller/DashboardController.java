package com.warung.haryati.controller;

import com.warung.haryati.App;
import com.warung.haryati.service.CSVService;
import com.warung.haryati.util.CurrencyUtil;
import com.warung.haryati.util.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {

    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private Text txtTotalTransaksi;
    @FXML private Text txtTotalProduk;
    @FXML private Text txtTotalPendapatan;
    @FXML private LineChart<String, Number> salesChart;
    
    @FXML private Button btnDashboard, btnProduk, btnTransaksi, btnAnalisis, btnLaporan;

    private CSVService csvService = new CSVService();

    @FXML
    public void initialize() {
        setActive(btnDashboard);
        refreshDashboard();
    }

    public void refreshDashboard() {
        loadStatistics();
        loadChartData();
    }

    private void loadStatistics() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total Transaksi
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM transaksi");
            if (rs.next()) txtTotalTransaksi.setText(String.valueOf(rs.getInt(1)));
            
            // Total Produk
            rs = stmt.executeQuery("SELECT COUNT(*) FROM produk");
            if (rs.next()) txtTotalProduk.setText(String.valueOf(rs.getInt(1)));
            
            // Total Pendapatan
            rs = stmt.executeQuery("SELECT SUM(subtotal) FROM detail_transaksi");
            if (rs.next()) {
                double total = rs.getDouble(1);
                txtTotalPendapatan.setText(CurrencyUtil.format(total));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String sql = "SELECT DATE_FORMAT(t.tanggal, '%Y-%m') as periode, SUM(dt.subtotal) as total " +
                         "FROM transaksi t JOIN detail_transaksi dt ON t.id = dt.transaksi_id " +
                         "GROUP BY periode ORDER BY periode ASC LIMIT 12";
            
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("periode"), rs.getDouble("total")));
            }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        salesChart.getData().clear();
        salesChart.getData().add(series);
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File CSV Dataset");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                csvService.importFromCSV(selectedFile);
                loadStatistics();
                loadChartData();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diimport!");
            } catch (IOException | SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal import: " + e.getMessage());
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

    @FXML private void showDashboard() { 
        setActive(btnDashboard); 
        refreshDashboard();
        contentArea.getChildren().setAll(dashboardView);
    }
    
    @FXML private void showProduk() { 
        setActive(btnProduk); 
        loadView("produk");
    }
    
    @FXML private void showTransaksi() { 
        setActive(btnTransaksi); 
        loadView("transaksi");
    }
    
    @FXML 
    private void showAnalisis() { 
        setActive(btnAnalisis); 
        loadView("analisis");
    }

    @FXML private void showLaporan() { setActive(btnLaporan); }

    private void loadView(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml + ".fxml"));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button activeBtn) {
        btnDashboard.getStyleClass().remove("sidebar-button-active");
        btnProduk.getStyleClass().remove("sidebar-button-active");
        btnTransaksi.getStyleClass().remove("sidebar-button-active");
        btnAnalisis.getStyleClass().remove("sidebar-button-active");
        btnLaporan.getStyleClass().remove("sidebar-button-active");
        
        activeBtn.getStyleClass().add("sidebar-button-active");
    }

    @FXML
    private void handleLogout() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
