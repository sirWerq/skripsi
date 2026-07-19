package com.warung.haryati.controller;

import com.warung.haryati.dao.DetailTransaksiDao;
import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.model.Produk;
import com.warung.haryati.model.Transaksi;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;

public class DetailTransaksiFormController {

    @FXML private ComboBox<Transaksi> cbTransaksi;
    @FXML private ComboBox<Produk> cbProduk;
    @FXML private Spinner<Integer> spinKuantitas;

    private DetailTransaksiDao detailTransaksiDao = new DetailTransaksiDao();
    private TransaksiDao transaksiDao = new TransaksiDao();
    private ProdukDao produkDao = new ProdukDao();
    
    private DetailTransaksi currentData;
    private boolean isSaved = false;

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadComboBoxData();
    }
    
    private void setupComboBoxes() {
        cbTransaksi.setConverter(new StringConverter<>() {
            @Override
            public String toString(Transaksi t) {
                return t == null ? "" : t.getId() + " - " + com.warung.haryati.util.DateUtil.formatShort(t.getTanggal());
            }

            @Override
            public Transaksi fromString(String string) {
                return null; // Not needed
            }
        });

        cbProduk.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produk p) {
                return p == null ? "" : p.getNamaBarang() + " (Rp " + p.getHargaJual() + ")";
            }

            @Override
            public Produk fromString(String string) {
                return null;
            }
        });
    }

    private void loadComboBoxData() {
        try {
            List<Transaksi> listT = transaksiDao.getAll();
            cbTransaksi.setItems(FXCollections.observableArrayList(listT));
            
            List<Produk> listP = produkDao.getAll();
            cbProduk.setItems(FXCollections.observableArrayList(listP));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDetailTransaksi(DetailTransaksi t) {
        this.currentData = t;
        
        for (Transaksi trx : cbTransaksi.getItems()) {
            if (trx.getId().equals(t.getTransaksiId())) {
                cbTransaksi.setValue(trx);
                break;
            }
        }
        
        for (Produk p : cbProduk.getItems()) {
            if (p.getId().equals(t.getProdukId())) {
                cbProduk.setValue(p);
                break;
            }
        }
        
        spinKuantitas.getValueFactory().setValue(t.getKuantitas());
    }

    @FXML
    private void handleSimpan() {
        Transaksi selectedTrx = cbTransaksi.getValue();
        Produk selectedProduk = cbProduk.getValue();
        
        if (selectedTrx == null || selectedProduk == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Transaksi dan Produk harus dipilih.");
            return;
        }

        int qty = spinKuantitas.getValue();
        double subtotal = qty * selectedProduk.getHargaJual();
        double laba = subtotal - (qty * selectedProduk.getHargaBeli());

        try {
            if (currentData == null) {
                currentData = new DetailTransaksi();
                currentData.setTransaksiId(selectedTrx.getId());
                currentData.setProdukId(selectedProduk.getId());
                currentData.setKuantitas(qty);
                currentData.setSubtotal(subtotal);
                currentData.setLaba(laba);
                detailTransaksiDao.insert(currentData);
            } else {
                currentData.setTransaksiId(selectedTrx.getId());
                currentData.setProdukId(selectedProduk.getId());
                currentData.setKuantitas(qty);
                currentData.setSubtotal(subtotal);
                currentData.setLaba(laba);
                detailTransaksiDao.update(currentData);
            }
            isSaved = true;
            closeStage();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleBatal() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cbTransaksi.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean isSaved() {
        return isSaved;
    }
}
