package com.warung.haryati.controller;

import com.warung.haryati.App;
import com.warung.haryati.service.CSVService;
import com.warung.haryati.util.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

public class DashboardController {

    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private Text txtTotalTransaksi;
    @FXML private Text txtTotalProduk;
    
    @FXML private Button btnDashboard, btnProduk, btnTransaksi, btnDetailTransaksi, btnAnalisis, btnLaporan;

    private CSVService csvService = new CSVService();

    @FXML
    public void initialize() {
        setActive(btnDashboard);
        refreshDashboard();
    }

    public void refreshDashboard() {
        loadStatistics();
    }

    private void loadStatistics() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM transaksi");
            if (rs.next()) txtTotalTransaksi.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM produk");
            if (rs.next()) txtTotalProduk.setText(String.valueOf(rs.getInt(1)));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File CSV Dataset");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                csvService.importFromCSV(selectedFile);
                
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO dashboard (ringkasan_produk, ringkasan_transaksi, file_dataset_excel) VALUES (?, ?, ?)";
                    try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                        insertStmt.setString(1, "Produk: " + txtTotalProduk.getText());
                        insertStmt.setString(2, "Transaksi: " + txtTotalTransaksi.getText());
                        insertStmt.setString(3, selectedFile.getName());
                        insertStmt.executeUpdate();
                    }
                }
                
                loadStatistics();
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

    @FXML private void showDetailTransaksi() { 
        setActive(btnDetailTransaksi); 
        loadView("detail_transaksi");
    }
    
    @FXML 
    private void showAnalisis() { 
        setActive(btnAnalisis); 
        loadView("analisis");
    }

    @FXML private void showLaporan() { 
        setActive(btnLaporan); 
        loadView("laporan");
    }

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
        btnDetailTransaksi.getStyleClass().remove("sidebar-button-active");
        btnAnalisis.getStyleClass().remove("sidebar-button-active");
        btnLaporan.getStyleClass().remove("sidebar-button-active");
        
        activeBtn.getStyleClass().add("sidebar-button-active");
    }

    @FXML
    private void handleLogout() {
        try {
            String idPemilik = com.warung.haryati.util.UserSession.getIdPemilik();
            if (idPemilik != null) {
                try (Connection conn = DBConnection.getConnection()) {
                    String idLogout = "LGO-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    String insertLogout = "INSERT INTO logout (id_logout, id_pemilik) VALUES (?, ?)";
                    try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertLogout)) {
                        insertStmt.setString(1, idLogout);
                        insertStmt.setString(2, idPemilik);
                        insertStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                com.warung.haryati.util.UserSession.clear();
            }
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
