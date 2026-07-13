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
        String nama = namaField.getText().trim();
        String hargaBeliStr = hargaBeliField.getText().trim();
        String hargaJualStr = hargaJualField.getText().trim();

        if (nama.isEmpty() || hargaBeliStr.isEmpty() || hargaJualStr.isEmpty()) {
            showAlert("Error", "Semua field harus diisi!");
            return;
        }

        try {
            double hargaBeli = Double.parseDouble(hargaBeliStr);
            double hargaJual = Double.parseDouble(hargaJualStr);

            if (hargaBeli < 0 || hargaJual < 0) {
                showAlert("Error", "Harga tidak boleh negatif!");
                return;
            }

            if (hargaJual < hargaBeli) {
                showAlert("Peringatan", "Harga jual lebih kecil dari harga beli. Lanjutkan?");
            }

            if (currentProduk == null) {
                Produk p = new Produk();
                p.setNamaBarang(nama);
                p.setHargaBeli(hargaBeli);
                p.setHargaJual(hargaJual);
                produkDao.insert(p);
            } else {
                currentProduk.setNamaBarang(nama);
                currentProduk.setHargaBeli(hargaBeli);
                currentProduk.setHargaJual(hargaJual);
                produkDao.update(currentProduk);
            }
            saved = true;
            closeStage();
        } catch (NumberFormatException e) {
            showAlert("Error", "Harga harus berupa angka yang valid!");
        } catch (SQLException e) {
            showAlert("Database Error", "Gagal menyimpan data: " + e.getMessage());
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
