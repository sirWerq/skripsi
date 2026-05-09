package com.warung.haryati.controller;

import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.model.Produk;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProdukFormController {

    @FXML private TextField namaField;
    @FXML private TextField hargaBeliField;
    @FXML private TextField hargaJualField;

    private ProdukDao produkDao = new ProdukDao();
    private boolean saved = false;
    private Produk currentProduk;

    public void setProduk(Produk p) {
        this.currentProduk = p;
        namaField.setText(p.getNamaBarang());
        hargaBeliField.setText(String.valueOf(p.getHargaBeli()));
        hargaJualField.setText(String.valueOf(p.getHargaJual()));
    }

    public boolean isSaved() { return saved; }

    @FXML
    private void handleSimpan() {
        if (namaField.getText().isEmpty() || hargaBeliField.getText().isEmpty() || hargaJualField.getText().isEmpty()) {
            showAlert("Error", "Semua field harus diisi!");
            return;
        }

        try {
            if (currentProduk == null) {
                Produk p = new Produk();
                p.setNamaBarang(namaField.getText());
                p.setHargaBeli(Double.parseDouble(hargaBeliField.getText()));
                p.setHargaJual(Double.parseDouble(hargaJualField.getText()));
                produkDao.insert(p);
            } else {
                currentProduk.setNamaBarang(namaField.getText());
                currentProduk.setHargaBeli(Double.parseDouble(hargaBeliField.getText()));
                currentProduk.setHargaJual(Double.parseDouble(hargaJualField.getText()));
                produkDao.update(currentProduk);
            }
            saved = true;
            closeStage();
        } catch (NumberFormatException e) {
            showAlert("Error", "Harga harus berupa angka!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleBatal() {
        closeStage();
    }

    private void closeStage() {
        ((Stage) namaField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
